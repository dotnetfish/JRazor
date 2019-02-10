package com.superstudio.language.java.parser;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.Tuple;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.language.java.symbols.JavaKeyword;
import com.superstudio.language.java.symbols.JavaSymbol;
import com.superstudio.language.java.symbols.JavaSymbolType;
import com.superstudio.language.java.tokenizer.JavaTokenizer;
import com.superstudio.web.RazorResources;
import com.superstudio.web.razor.editor.EditorHints;
import com.superstudio.web.razor.editor.ImplicitExpressionEditHandler;
import com.superstudio.web.razor.editor.SpanEditHandler;
import com.superstudio.web.razor.generator.*;
import com.superstudio.web.razor.parser.*;
import com.superstudio.web.razor.parser.syntaxTree.*;
import com.superstudio.web.razor.text.LocationTagged;
import com.superstudio.web.razor.text.SourceLocation;
import com.superstudio.web.razor.tokenizer.symbols.ISymbol;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public class JavaCodeParser extends TokenizerBackedParser<JavaTokenizer, JavaSymbol, JavaSymbolType> {
    private void setupDirectives() {
        mapDirectives(() -> inheritsDirective(), SyntaxConstants.Java.InheritsKeyword);
        mapDirectives(() -> functionsDirective(), SyntaxConstants.Java.FunctionsKeyword);
        mapDirectives(() -> sectionDirective(), SyntaxConstants.Java.SectionKeyword);
        mapDirectives(() -> helperDirective(), SyntaxConstants.Java.HelperKeyword);
        mapDirectives(() -> layoutDirective(), SyntaxConstants.Java.LayoutKeyword);
        mapDirectives(() -> sessionStateDirective(), SyntaxConstants.Java.SessionStateKeyword);
    }

    protected void layoutDirective() {
        assertDirective(SyntaxConstants.Java.LayoutKeyword);
        acceptAndMoveNext();
        getContext().getCurrentBlock().setType(BlockType.Directive);

        // accept spaces, but not newlines
        boolean foundSomeWhitespace = at(JavaSymbolType.WhiteSpace);
        acceptWhile(JavaSymbolType.WhiteSpace);
        output(SpanKind.MetaCode, foundSomeWhitespace ? AcceptedCharacters.None : AcceptedCharacters.Any);

        // First non-whitespace character starts the Layout Page, then newline
        // ends it
        acceptUntil(JavaSymbolType.NewLine);
        getSpan().setCodeGenerator(new SetLayoutCodeGenerator(getSpan().getContent().toString()));
        EditorHints hints = EditorHints.forValue(EditorHints.LayoutPage.getValue() | EditorHints.VirtualPath.getValue());
        getSpan().getEditHandler().setEditorHints(hints);
        boolean foundNewline = optional(JavaSymbolType.NewLine);
        addMarkerSymbolIfNecessary();
        output(SpanKind.MetaCode, foundNewline ? AcceptedCharacters.None : AcceptedCharacters.Any);
    }

    protected void sessionStateDirective() {
        assertDirective(SyntaxConstants.Java.SessionStateKeyword);
        acceptAndMoveNext();

        sessionStateDirectiveCore();
    }

    protected final void sessionStateDirectiveCore() {

        sessionStateTypeDirective(RazorResources.getResource(RazorResources.ParserEror_SessionDirectiveMissingValue),
                (key, value) -> new RazorDirectiveAttributeCodeGenerator(key, value));
    }

    protected final void sessionStateTypeDirective(String noValueError,
                                                   BiFunction<String, String, SpanCodeGenerator> createCodeGenerator) {
        // Set the block type
        getContext().getCurrentBlock().setType(BlockType.Directive);

        // accept whitespace
        JavaSymbol remainingWs = acceptSingleWhiteSpaceCharacter();

        if (getSpan().getSymbols().size() > 1) {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }

        output(SpanKind.MetaCode);

        if (remainingWs != null) {
            accept(remainingWs);
        }
        acceptWhile(isSpacingToken(false, true));

        // parse a Type Name
        if (!validSessionStateValue()) {
            getContext().onError(getCurrentLocation().clone(), noValueError);
        }

        // Pull out the type name

        String sessionStateValue=(getSpan().getSymbols().stream().collect(
                StringBuilder::new,(StringBuilder builder,ISymbol sym)->builder.append(sym.getContent()),StringBuilder::append
        )).toString();


        // Set up code generation
        getSpan().setCodeGenerator(createCodeGenerator.apply(SyntaxConstants.Java.SessionStateKeyword, sessionStateValue));

        // output the span and finish the block
        completeBlock();
        output(SpanKind.Code);
    }

    protected boolean validSessionStateValue() {
        return optional(JavaSymbolType.Identifier);
    }


    protected void helperDirective() {
        boolean nested = getContext().isWithin(BlockType.Helper);

        // Set the block and span type
        getContext().getCurrentBlock().setType(BlockType.Helper);

        // Verify we're on "helper" and accept
        assertDirective(SyntaxConstants.Java.HelperKeyword);
        Block block = new Block(getCurrentSymbol().getContent().toString().toLowerCase(), getCurrentLocation().clone());
        acceptAndMoveNext();

        if (nested) {
            getContext().onError(getCurrentLocation().clone(), RazorResources.getResource(RazorResources.ParseError_Helpers_Cannot_Be_Nested));
        }

        // accept a single whitespace character if present, if not, we should
        // stop now
        if (!at(JavaSymbolType.WhiteSpace)) {
            String error;
            if (at(JavaSymbolType.NewLine)) {
                error = RazorResources.getResource(RazorResources.ErrorComponent_Newline);
            } else if (getEndOfFile()) {
                error = RazorResources.getResource(RazorResources.ErrorComponent_EndOfFile);
            } else {
                error = String.format(RazorResources.getResource(RazorResources.ErrorComponent_Character), getCurrentSymbol().getContent());
            }

            getContext().onError(getCurrentLocation().clone(),
                    RazorResources.getResource(RazorResources.ParseError_Unexpected_Character_At_Helper_Name_Start), error);
            putCurrentBack();
            output(SpanKind.MetaCode);
            return;
        }

        JavaSymbol remainingWs = acceptSingleWhiteSpaceCharacter();

        // output metacode and continue
        output(SpanKind.MetaCode);
        if (remainingWs != null) {
            accept(remainingWs);
        }
        acceptWhile(isSpacingToken(false, true)); // Don't accept newlines.

        // Expecting an identifier (helper name)
        boolean errorReported = !required(JavaSymbolType.Identifier, true,
                RazorResources.getResource(RazorResources.ParseError_Unexpected_Character_At_Helper_Name_Start));
        if (!errorReported) {
            assertSymbol(JavaSymbolType.Identifier);
            acceptAndMoveNext();
        }

        acceptWhile(isSpacingToken(false, true));

        // Expecting parameter list start: "("
        SourceLocation bracketErrorPos = getCurrentLocation().clone();
        if (!optional(JavaSymbolType.LeftParenthesis)) {
            if (!errorReported) {
                errorReported = true;
                getContext().onError(getCurrentLocation().clone(),
                        RazorResources.getResource(RazorResources.ParseError_MissingCharAfterHelperName), "(");
            }
        } else {
            SourceLocation bracketStart = getCurrentLocation().clone();
            if (!balance(BalancingModes.NoErrorOnFailure, JavaSymbolType.LeftParenthesis,
                    JavaSymbolType.RightParenthesis, bracketStart)) {
                errorReported = true;
                getContext().onError(bracketErrorPos, RazorResources.getResource(RazorResources.ParseError_UnterminatedHelperParameterList));
            }
            optional(JavaSymbolType.RightParenthesis);
        }

        int bookmark = getCurrentLocation().getAbsoluteIndex();
        Iterable<JavaSymbol> ws = readWhile(isSpacingToken(true, true));

        // Expecting a "{"
        SourceLocation errorLocation = getCurrentLocation().clone();
        boolean headerComplete = at(JavaSymbolType.LeftBrace);
        if (headerComplete) {
            accept(ws);
            acceptAndMoveNext();
        } else {
            getContext().getSource().setPosition(bookmark);
            nextToken();
            acceptWhile(isSpacingToken(false, true));
            if (!errorReported) {
                getContext().onError(errorLocation, RazorResources.getResource(RazorResources.ParseError_MissingCharAfterHelperParameters),
                        getLanguage().getSample(JavaSymbolType.LeftBrace));
            }
        }

        // Grab the signature and build the code generator
        addMarkerSymbolIfNecessary();
        LocationTagged<String> signature = getSpan().getContent();
        HelperCodeGenerator blockGen = new HelperCodeGenerator(signature, headerComplete);
        getContext().getCurrentBlock().setCodeGenerator(blockGen);

        // The block will generate appropriate code,
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);

        if (!headerComplete) {
            completeBlock();
            output(SpanKind.Code);
            return;
        } else {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
            output(SpanKind.Code);
        }

        AutoCompleteEditHandler bodyEditHandler =
                new AutoCompleteEditHandler(p -> getLanguage().tokenizeString(p));

        try (AutoCloseable disposable = pushSpanConfig((p) -> defaultSpanConfig(p))) {

            try (AutoCloseable blockDispose = getContext().startBlock(BlockType.Statement)) {
                getSpan().setEditHandler(bodyEditHandler);
                codeBlock(false, block);
                completeBlock(true);
                output(SpanKind.Code);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initialize(getSpan());

        ensureCurrent();

        getSpan().setCodeGenerator(SpanCodeGenerator.Null); // The block will
        // generate the
        // footer code.
        if (!optional(JavaSymbolType.RightBrace)) {
            // The } is missing, so set the initial signature span to use it as
            // an autocomplete string
            bodyEditHandler.setAutoCompleteString("}");

            // Need to be able to accept anything to properly handle the
            // autocomplete
            bodyEditHandler.setAcceptedCharacters(AcceptedCharacters.Any);
        } else {
            blockGen.setFooter(getSpan().getContent());
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }
        completeBlock();
        output(SpanKind.Code);
    }

    protected void sectionDirective() {
        boolean nested = getContext().isWithin(BlockType.Section);
        boolean errorReported = false;

        // Set the block and span type
        getContext().getCurrentBlock().setType(BlockType.Section);

        // Verify we're on "section" and accept
        assertDirective(SyntaxConstants.Java.SectionKeyword);
        acceptAndMoveNext();

        if (nested) {
            getContext().onError(getCurrentLocation().clone(), String.format(
                    RazorResources.getResource(RazorResources.ParseError_Sections_Cannot_Be_Nested), RazorResources.getResource(RazorResources.SectionExample_CS)));
            errorReported = true;
        }

        Iterable<JavaSymbol> ws = readWhile(isSpacingToken(true, false));

        // Get the section name
        String sectionName = "";
        if (!required(JavaSymbolType.Identifier, true,
                RazorResources.getResource(RazorResources.ParseError_Unexpected_Character_At_Section_Name_Start))) {
            if (!errorReported) {
                errorReported = true;
            }

            putCurrentBack();
            putBack(ws);
            acceptWhile(isSpacingToken(false, false));
        } else {
            accept(ws);
            sectionName = getCurrentSymbol().getContent();
            acceptAndMoveNext();
        }
        getContext().getCurrentBlock().setCodeGenerator(new SectionCodeGenerator(sectionName));

        SourceLocation errorLocation = getCurrentLocation().clone();
        ws = readWhile(isSpacingToken(true, false));

        // Get the starting brace
        boolean sawStartingBrace = at(JavaSymbolType.LeftBrace);
        if (!sawStartingBrace) {
            if (!errorReported) {
                errorReported = true;
                getContext().onError(errorLocation, RazorResources.getResource(RazorResources.ParseError_MissingOpenBraceAfterSection));
            }

            putCurrentBack();
            putBack(ws);
            acceptWhile(isSpacingToken(false, false));
            optional(JavaSymbolType.NewLine);
            output(SpanKind.MetaCode);
            completeBlock();
            return;
        } else {
            accept(ws);
        }

        // Set up edit handler
        AutoCompleteEditHandler tempVar = new AutoCompleteEditHandler(p -> getLanguage().tokenizeString(p));
        tempVar.setAutoCompleteAtEndOfSpan(true);
        AutoCompleteEditHandler editHandler = tempVar;

        getSpan().setEditHandler(editHandler);
        getSpan().accept(getCurrentSymbol());

        // output Metacode then switch to section parser
        output(SpanKind.MetaCode);
        sectionBlock("{", "}", true);

        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        // Check for the terminating "}"
        if (!optional(JavaSymbolType.RightBrace)) {
            editHandler.setAutoCompleteString("}");
            getContext().onError(getCurrentLocation().clone(), RazorResources.getResource(RazorResources.ParseError_Expected_X),
                    getLanguage().getSample(JavaSymbolType.RightBrace));
        } else {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }
        completeBlock(false, true);
        output(SpanKind.MetaCode);
        return;
    }

    protected void functionsDirective() {
        // Set the block type
        getContext().getCurrentBlock().setType(BlockType.Functions);

        // Verify we're on "functions" and accept
        assertDirective(SyntaxConstants.Java.FunctionsKeyword);
        Block block = new Block(getCurrentSymbol());
        acceptAndMoveNext();

        acceptWhile(isSpacingToken(true, false));

        if (!at(JavaSymbolType.LeftBrace)) {
            getContext().onError(getCurrentLocation().clone(), RazorResources.getResource(RazorResources.ParseError_Expected_X),
                    getLanguage().getSample(JavaSymbolType.LeftBrace));
            completeBlock();
            output(SpanKind.MetaCode);
            return;
        } else {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }

        // Capture start point and continue
        SourceLocation blockStart = getCurrentLocation().clone();
        acceptAndMoveNext();

        // output what we've seen and continue
        output(SpanKind.MetaCode);

        AutoCompleteEditHandler editHandler = new AutoCompleteEditHandler(p -> getLanguage().tokenizeString(p));
        getSpan().setEditHandler(editHandler);

        balance(BalancingModes.NoErrorOnFailure, JavaSymbolType.LeftBrace, JavaSymbolType.RightBrace, blockStart);
        getSpan().setCodeGenerator(new TypeMemberCodeGenerator());
        if (!at(JavaSymbolType.RightBrace)) {
            editHandler.setAutoCompleteString("}");
            getContext().onError(block.getStart().clone(),
                    RazorResources.getResource(RazorResources.ParseError_Expected_CloseBracket_Before_EOF), block.getName(), "}", "{");
            completeBlock();
            output(SpanKind.Code);
        } else {
            output(SpanKind.Code);
            assertSymbol(JavaSymbolType.RightBrace);
            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
            acceptAndMoveNext();
            completeBlock();
            output(SpanKind.MetaCode);
        }
    }

    protected void inheritsDirective() {
        // Verify we're on the right keyword and accept
        assertDirective(SyntaxConstants.Java.InheritsKeyword);
        acceptAndMoveNext();

        inheritsDirectiveCore();
    }


    protected final void assertDirective(String directive) {
        assertSymbol(JavaSymbolType.Identifier);
        assert getCurrentSymbol().getContent().equals(directive);
    }

    protected final void inheritsDirectiveCore() {
        baseTypeDirective(RazorResources.getResource(RazorResources.ParseError_InheritsKeyword_Must_Be_Followed_By_TypeName),
                baseType -> new SetBaseTypeCodeGenerator(baseType));
    }

    protected final void baseTypeDirective(String noTypeNameError,
                                           Function<String, SpanCodeGenerator> createCodeGenerator) {
        // Set the block type
        getContext().getCurrentBlock().setType(BlockType.Directive);

        // accept whitespace
        JavaSymbol remainingWs = acceptSingleWhiteSpaceCharacter();

        if (getSpan().getSymbols().size() > 1) {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }

        output(SpanKind.MetaCode);

        if (remainingWs != null) {
            accept(remainingWs);
        }
        acceptWhile(isSpacingToken(false, true));

        if (getEndOfFile() || at(JavaSymbolType.WhiteSpace) || at(JavaSymbolType.NewLine)) {
            getContext().onError(getCurrentLocation().clone(), noTypeNameError);
        }

        // parse to the end of the line
        acceptUntil(JavaSymbolType.NewLine);
        if (!getContext().getDesignTimeMode()) {
            // We want the newline to be treated as code, but it causes issues
            // at design-time.
            optional(JavaSymbolType.NewLine);
        }

        // Pull out the type name
        String baseType = getSpan().getContent().toString();

        // Set up code generation
        getSpan().setCodeGenerator(createCodeGenerator.apply(baseType.trim()));

        // output the span and finish the block
        completeBlock();
        output(SpanKind.Code);
    }

    private void setUpKeywords() {
        mapKeywords((p) -> conditionalBlock(p), JavaKeyword.For, JavaKeyword.Foreach, JavaKeyword.While,
                JavaKeyword.Switch, JavaKeyword.Lock);
        mapKeywords((p) -> caseStatement(p), false, JavaKeyword.Case, JavaKeyword.Default);
        mapKeywords((p) -> ifStatement(p), JavaKeyword.If);
        mapKeywords((p) -> tryKeyword(p), JavaKeyword.Try);
        mapKeywords((p) -> importDeclaration(p), JavaKeyword.Import);
        mapKeywords((p) -> doStatement(p), JavaKeyword.Do);
        mapKeywords((p) -> reservedDirective(p), JavaKeyword.Namespace, JavaKeyword.Class);
    }

    protected void reservedDirective(boolean topLevel) {
        getContext().onError(getCurrentLocation().clone(),
                String.format(RazorResources.getResource(RazorResources.ParseError_ReservedWord), getCurrentSymbol().getContent()));
        acceptAndMoveNext();
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        getContext().getCurrentBlock().setType(BlockType.Directive);
        completeBlock();
        output(SpanKind.MetaCode);
    }

    private void keywordBlock(boolean topLevel) {
        handleKeyword(topLevel, () -> {
            getContext().getCurrentBlock().setType(BlockType.Expression);
            getContext().getCurrentBlock().setCodeGenerator(new ExpressionCodeGenerator());
            implicitExpression();
        });
    }

    private void caseStatement(boolean topLevel) {
        assertSymbol(JavaSymbolType.Keyword);
        assert getCurrentSymbol().getKeyword() != null && (getCurrentSymbol().getKeyword() == JavaKeyword.Case
                || getCurrentSymbol().getKeyword() == JavaKeyword.Default);
        acceptUntil(JavaSymbolType.Colon);
        optional(JavaSymbolType.Colon);
    }

    private void doStatement(boolean topLevel) {
        Assert(JavaKeyword.Do);
        unconditionalBlock();
        whileClause();
        if (topLevel) {
            completeBlock();
        }
    }

    private void whileClause() {
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
        Iterable<JavaSymbol> ws = skipToNextImportantToken();

        if (at(JavaKeyword.While)) {
            accept(ws);
            Assert(JavaKeyword.While);
            acceptAndMoveNext();
            acceptWhile(isSpacingToken(true, true));
            if (acceptCondition() && optional(JavaSymbolType.Semicolon)) {
                getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
            }
        } else {
            putCurrentBack();
            putBack(ws);
        }
    }


    private void tryKeyword(boolean topLevel) {//try(){} in java 8
        Assert(JavaKeyword.Try);
        Block block = new Block(getCurrentSymbol());
        acceptAndMoveNext();
        acceptWhile(isSpacingToken(false, true));

        if (at(JavaSymbolType.LeftParenthesis)) {
            // try ( =-> try statement
            usingStatement(block);
        } else if (at(JavaSymbolType.Identifier)) {
            //try {  -> try{} catch..
            tryStatement(topLevel);
            /*if (!topLevel) {
				getContext().onError(block.getStart().clone(),
						RazorResources.getResource(RazorResources.ParseError_NamespaceImportAndTypeAlias_Cannot_Exist_Within_CodeBlock());
				standardStatement();
			} else {
				//UsingDeclaration();
				
			}*/
        }

        if (topLevel) {
            completeBlock();
        }
    }

    private void importDeclaration(boolean isTopLevel) {

        Assert(JavaKeyword.Import);
        Block block = new Block(getCurrentSymbol());

        nextToken();
        acceptWhile(isSpacingToken(false, true));
        if (required(JavaSymbolType.Identifier, true, "import 关键字不正确")) {
            // Set block type to directive
            if (!isTopLevel) {//import should be topLevel @import
                getContext().onError(block.getStart().clone(),
                        RazorResources.getResource(RazorResources.ParseError_NamespaceImportAndTypeAlias_Cannot_Exist_Within_CodeBlock));
                standardStatement();

            } else {
                getContext().getCurrentBlock().setType(BlockType.Directive);

                // parse a type name
                assertSymbol(JavaSymbolType.Identifier);
                namespaceOrTypeName();
                Iterable<JavaSymbol> ws = readWhile(isSpacingToken(true, true));
                if (at(JavaSymbolType.Assign)) {
                    // Alias
                    accept(ws);
                    assertSymbol(JavaSymbolType.Assign);
                    acceptAndMoveNext();

                    acceptWhile(isSpacingToken(true, true));

                    // One more namespace or type name
                    namespaceOrTypeName();
                } else {
                    putCurrentBack();
                    putBack(ws);
                }

                getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.AnyExceptNewline);

                // methods are not converted
                getSpan().setCodeGenerator(new AddImportCodeGenerator(
                        getSpan().getContent(syms -> CollectionHelper.skip(syms, 1)),
                        SyntaxConstants.Java.UsingKeywordLength)); // Skip "using"

                // optional ";"
                if (ensureCurrent()) {
                    optional(JavaSymbolType.Semicolon);
                }
            }
            if (isTopLevel) {
                completeBlock();
            }
        }


    }

    private boolean namespaceOrTypeName() {
        if (optional(JavaSymbolType.Identifier) || optional(JavaSymbolType.Keyword) || optional(JavaSymbolType.Star)) {
            //optional(JavaSymbolType.Star); // .*
            if (optional(JavaSymbolType.DoubleColon)) {
                if (!optional(JavaSymbolType.Identifier)) {
                    optional(JavaSymbolType.Keyword);
                }
            }
            //in java not <>
			/*if (at(JavaSymbolType.LessThan)) {
				typeArgumentList();
			}*/
            if (optional(JavaSymbolType.Dot)) {
                namespaceOrTypeName();
            }
			/*while (at(JavaSymbolType.LeftBracket)) {
				balance(BalancingModes.None);
				optional(JavaSymbolType.RightBracket);
			}*/
            return true;
        } else {
            return false;
        }
    }

    private void typeArgumentList() {
        assertSymbol(JavaSymbolType.LessThan);
        balance(BalancingModes.None);
        optional(JavaSymbolType.GreaterThan);
    }

    private void usingStatement(Block block) {
        assertSymbol(JavaSymbolType.LeftParenthesis);

        // parse condition
        if (acceptCondition()) {
            acceptWhile(isSpacingToken(true, true));

            // parse code block
            expectCodeBlock(block);
        }
    }

    private void tryStatement(boolean topLevel) {
        Assert(JavaKeyword.Try);
        unconditionalBlock();
        afterTryClause();
        if (topLevel) {
            completeBlock();
        }

    }

    private void ifStatement(boolean topLevel) {
        Assert(JavaKeyword.If);
        conditionalBlock(false);
        afterIfClause();
        if (topLevel) {
            completeBlock();
        }
    }

    private void afterTryClause() {
        // Grab whitespace
        Iterable<JavaSymbol> ws = skipToNextImportantToken();

        // Check for a catch or finally part
        if (at(JavaKeyword.Catch)) {
            accept(ws);
            Assert(JavaKeyword.Catch);
            conditionalBlock(false);
            afterTryClause();
        } else if (at(JavaKeyword.Finally)) {
            accept(ws);
            Assert(JavaKeyword.Finally);
            unconditionalBlock();
        } else {
            // Return whitespace and end the block
            putCurrentBack();
            putBack(ws);
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
        }
    }

    private void afterIfClause() {
        // Grab whitespace and razor comments
        Iterable<JavaSymbol> ws = skipToNextImportantToken();

        // Check for an else part
        if (at(JavaKeyword.Else)) {
            accept(ws);
            Assert(JavaKeyword.Else);
            elseClause();
        } else {
            // No else, return whitespace
            putCurrentBack();
            putBack(ws);
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
        }
    }

    private void elseClause() {
        if (!at(JavaKeyword.Else)) {
            return;
        }
        Block block = new Block(getCurrentSymbol());

        acceptAndMoveNext();
        acceptWhile(isSpacingToken(true, true));
        if (at(JavaKeyword.If)) {
            // ElseIf
            block.setName(SyntaxConstants.Java.ElseIfKeyword);
            conditionalBlock(block);
            afterIfClause();
        } else if (!getEndOfFile()) {
            // Else
            expectCodeBlock(block);
        }
    }

    private void expectCodeBlock(Block block) {
        if (!getEndOfFile()) {
            // Check for "{" to make sure we're at a block
            if (!at(JavaSymbolType.LeftBrace)) {
                getContext().onError(getCurrentLocation().clone(),
                        RazorResources.getResource(RazorResources.ParseError_SingleLine_ControlFlowStatements_Not_Allowed),
                        getLanguage().getSample(JavaSymbolType.LeftBrace), getCurrentSymbol().getContent());
            }

            // parse the statement and then we're done
            statement(block);
        }
    }

    private void unconditionalBlock() {
        assertSymbol(JavaSymbolType.Keyword);
        Block block = new Block(getCurrentSymbol());
        acceptAndMoveNext();
        acceptWhile(isSpacingToken(true, true));
        expectCodeBlock(block);
    }

    private void conditionalBlock(boolean topLevel) {
        assertSymbol(JavaSymbolType.Keyword);
        Block block = new Block(getCurrentSymbol());
        conditionalBlock(block);
        if (topLevel) {
            completeBlock();
        }
    }

    private void conditionalBlock(Block block) {
        acceptAndMoveNext();
        acceptWhile(isSpacingToken(true, true));

        // parse the condition, if present (if not present, we'll let the C#
        // compiler complain)
        if (acceptCondition()) {
            acceptWhile(isSpacingToken(true, true));
            expectCodeBlock(block);
        }
    }

    private boolean acceptCondition() {
        if (at(JavaSymbolType.LeftParenthesis)) {
            BalancingModes modes = BalancingModes.forValue(BalancingModes.BacktrackOnFailure.getValue() | BalancingModes.AllowCommentsAndTemplates.getValue());
            boolean complete = balance(modes);
            if (!complete) {
                acceptUntil(JavaSymbolType.NewLine);
            } else {
                optional(JavaSymbolType.RightParenthesis);
            }
            return complete;
        }
        return true;
    }

    private void statement() {
        statement(null);
    }

    private void statement(Block block) {
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);

        // accept whitespace but always keep the last whitespace node so we can
        // put it back if necessary
        JavaSymbol lastWs = acceptWhiteSpaceInLines();
        assert lastWs == null || (lastWs.getStart().getAbsoluteIndex()
                + lastWs.getContent().length() == getCurrentLocation().getAbsoluteIndex());

        if (getEndOfFile()) {
            if (lastWs != null) {
                accept(lastWs);
            }
            return;
        }

        JavaSymbolType type = getCurrentSymbol().getType();
        SourceLocation loc = getCurrentLocation().clone();

        boolean isSingleLineMarkup = type == JavaSymbolType.Transition && nextIs(JavaSymbolType.Colon);
        boolean isMarkup = isSingleLineMarkup || type == JavaSymbolType.LessThan
                || (type == JavaSymbolType.Transition && nextIs(JavaSymbolType.LessThan));

        if (getContext().getDesignTimeMode() || !isMarkup) {
            // CODE owns whitespace, MARKUP owns it ONLY in DesignTimeMode.
            if (lastWs != null) {
                accept(lastWs);
            }
        } else {
            // MARKUP owns whitespace EXCEPT in DesignTimeMode.
            putCurrentBack();
            putBack(lastWs);
        }

        if (isMarkup) {
            if (type == JavaSymbolType.Transition && !isSingleLineMarkup) {
                getContext().onError(loc,
                        RazorResources.getResource(RazorResources.ParseError_AtInCode_Must_Be_Followed_By_Colon_Paren_Or_Identifier_Start));
            }

            // Markup block
            output(SpanKind.Code);
            if (getContext().getDesignTimeMode() && getCurrentSymbol() != null
                    && (getCurrentSymbol().getType() == JavaSymbolType.LessThan
                    || getCurrentSymbol().getType() == JavaSymbolType.Transition)) {
                putCurrentBack();
            }
            otherParserBlock();
        } else {
            // What kind of statement is this?
            handleStatement(block, type);
        }
    }

    private void handleStatement(Block block, JavaSymbolType type) {
        switch (type) {
            case RazorCommentTransition:
                output(SpanKind.Code);
                razorComment();
                statement(block);
                break;
            case LeftBrace:
                // Verbatim Block
                block = (block != null) ? block
                        : new Block(RazorResources.getResource(RazorResources.BlockName_Code), getCurrentLocation().clone());
                acceptAndMoveNext();
                codeBlock(block);
                break;
            case Keyword:
                // Keyword block
                handleKeyword(false, () -> standardStatement());
                break;
            case Transition:
                // Embedded Expression block
                embeddedExpression();
                break;
            case RightBrace:
                // Possible end of Code Block, just run the continuation
                break;
            case Comment:
                acceptAndMoveNext();
                break;
            default:
                // Other statement
                standardStatement();
                break;
        }
    }

    private void embeddedExpression() {
        // First, verify the type of the block
        assertSymbol(JavaSymbolType.Transition);
        JavaSymbol transition = getCurrentSymbol();
        nextToken();

        if (at(JavaSymbolType.Transition)) {
            // Escaped "@"
            output(SpanKind.Code);

            // output "@" as hidden span
            accept(transition);
            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
            output(SpanKind.Code);

            assertSymbol(JavaSymbolType.Transition);
            acceptAndMoveNext();
            standardStatement();
        } else {
            // Throw errors as necessary, but continue parsing
            if (at(JavaSymbolType.Keyword)) {
                getContext().onError(getCurrentLocation().clone(),
                        RazorResources.getResource(RazorResources.ParseError_Unexpected_Keyword_After_At),
                        JavaLanguageCharacteristics.getKeyword(getCurrentSymbol().getKeyword()));
            } else if (at(JavaSymbolType.LeftBrace)) {
                getContext().onError(getCurrentLocation().clone(),
                        RazorResources.getResource(RazorResources.ParseError_Unexpected_Nested_CodeBlock));
            }

            // @( or @foo - Nested expression, parse a child block
            putCurrentBack();
            putBack(transition);

            // Before exiting, add a marker span if necessary
            addMarkerSymbolIfNecessary();

            nestedBlock();
        }
    }

    private void standardStatement() {
        while (!getEndOfFile()) {
            int bookmark = getCurrentLocation().getAbsoluteIndex();

            // methods are not converted
            Iterable<JavaSymbol> read = readWhile(sym -> sym.getType() != JavaSymbolType.Semicolon
                    && sym.getType() != JavaSymbolType.RazorCommentTransition
                    && sym.getType() != JavaSymbolType.Transition && sym.getType() != JavaSymbolType.LeftBrace
                    && sym.getType() != JavaSymbolType.LeftParenthesis
                    && sym.getType() != JavaSymbolType.LeftBracket && sym.getType() != JavaSymbolType.RightBrace);
            if (at(JavaSymbolType.LeftBrace) || at(JavaSymbolType.LeftParenthesis)
                    || at(JavaSymbolType.LeftBracket)) {
                accept(read);
                BalancingModes modes = BalancingModes.forValue(BalancingModes.AllowCommentsAndTemplates.getValue()
                        | BalancingModes.BacktrackOnFailure.getValue());
                if (balance(modes)) {
                    optional(JavaSymbolType.RightBrace);
                } else {
                    // Recovery
                    acceptUntil(JavaSymbolType.LessThan, JavaSymbolType.RightBrace);
                    return;
                }
            } else if (at(JavaSymbolType.Transition) && (nextIs(JavaSymbolType.LessThan, JavaSymbolType.Colon))) {
                accept(read);
                output(SpanKind.Code);
                template();
            } else if (at(JavaSymbolType.RazorCommentTransition)) {
                accept(read);
                razorComment();
            } else if (at(JavaSymbolType.Semicolon)) {
                accept(read);
                acceptAndMoveNext();
                return;
            } else if (at(JavaSymbolType.RightBrace)) {
                accept(read);
                return;
            } else {
                getContext().getSource().setPosition(bookmark);
                nextToken();
                acceptUntil(JavaSymbolType.LessThan, JavaSymbolType.RightBrace);
                return;
            }
        }
    }

    private void codeBlock(Block block) {
        codeBlock(true, block);
    }

    private void codeBlock(boolean acceptTerminatingBrace, Block block) {
        ensureCurrent();
        while (!getEndOfFile() && !at(JavaSymbolType.RightBrace)) {
            // parse a statement, then return here
            statement();
            ensureCurrent();
        }

        if (getEndOfFile()) {
            getContext().onError(block.getStart().clone(),
                    RazorResources.getResource(RazorResources.ParseError_Expected_CloseBracket_Before_EOF), block.getName(), '}', '{');
        } else if (acceptTerminatingBrace) {
            assertSymbol(JavaSymbolType.RightBrace);
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
            acceptAndMoveNext();
        }
    }

    private void handleKeyword(boolean topLevel, Runnable fallback) {
        assert getCurrentSymbol().getType() == JavaSymbolType.Keyword && getCurrentSymbol().getKeyword() != null;
        Consumer<Boolean> handler = null;
        JavaKeyword keyword = getCurrentSymbol().getKeyword();


        if ((handler = _keywordParsers.get(keyword)) != null) {

            handler.accept(topLevel);
        } else {

            fallback.run();
        }

    }

    private Iterable<JavaSymbol> skipToNextImportantToken() {
        while (!getEndOfFile()) {
            Iterable<JavaSymbol> ws = readWhile(isSpacingToken(true, true));
            if (at(JavaSymbolType.RazorCommentTransition)) {
                accept(ws);
                getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
                razorComment();
            } else {
                return ws;
            }
        }
        return Collections.emptyList();
    }


    @Override
    protected void outputSpanBeforeRazorComment() {
        addMarkerSymbolIfNecessary();
        output(SpanKind.Code);
    }

    protected static class Block {
        public Block(String name, SourceLocation start) {
            setName(name);
            setStart(start);
        }

        public Block(JavaSymbol symbol) {
            this(getName(symbol), symbol.getStart().clone());
        }

        private String privateName;

        public final String getName() {
            return privateName;
        }

        public final void setName(String value) {
            privateName = value;
        }

        private SourceLocation privateStart;

        public final SourceLocation getStart() {
            return privateStart;
        }

        public final void setStart(SourceLocation value) {
            privateStart = value;
        }

        private static String getName(JavaSymbol sym) {
            if (sym.getType() == JavaSymbolType.Keyword) {
                return JavaLanguageCharacteristics.getKeyword(sym.getKeyword());
            }
            return sym.getContent();
        }
    }


    public static Set<String> DefaultKeywords = new java.util.HashSet<String>(java.util.Arrays
            .asList(new String[]{"if", "do", "try", "for", "foreach", "while", "switch", "lock", "import", "section",
                    "inherits", "helper", "functions", "package", "class", "layout", "sessionstate"}));

    private java.util.HashMap<String, Runnable> _directiveParsers = new java.util.HashMap<String, Runnable>();
    private java.util.HashMap<JavaKeyword, Consumer<Boolean>> _keywordParsers = new java.util.HashMap<JavaKeyword, Consumer<Boolean>>();

    public JavaCodeParser() {
        setKeywords(new java.util.HashSet<>());
        setUpKeywords();
        setupDirectives();
    }

    private Set<String> privateKeywords;

    protected final Set<String> getKeywords() {
        return privateKeywords;
    }

    private void setKeywords(Set<String> value) {
        privateKeywords = value;
    }

    private boolean privateIsNested;

    public final boolean getIsNested() {
        return privateIsNested;
    }

    public final void setIsNested(boolean value) {
        privateIsNested = value;
    }

    @Override
    protected ParserBase getOtherParser() {
        return getContext().getMarkupParser();
    }

    @Override
    protected LanguageCharacteristics<JavaTokenizer, JavaSymbol, JavaSymbolType> getLanguage() {
        return JavaLanguageCharacteristics.getInstance();
    }

    protected final void mapDirectives(Runnable handler, String... directives) {
       // java.util.function.Operator
      //Runnable  act=() -> System.out.print("dd");
      //act.execute();
        for (String directive : directives) {
            _directiveParsers.put(directive, handler);
            getKeywords().add(directive);
        }
    }

    protected final Runnable getDirectiveHandler(String directive) {
        //handler.setRefObj(_directiveParsers.get(directive));
        return _directiveParsers.get(directive);
    }


    private void mapKeywords(Consumer<Boolean> handler, JavaKeyword... keywords) {
        mapKeywords(handler, true, keywords);
    }

    private void mapKeywords(Consumer<Boolean> handler, boolean topLevel, JavaKeyword... keywords) {
        for (JavaKeyword keyword : keywords) {
            _keywordParsers.put(keyword, handler);
            if (topLevel) {
                getKeywords().add(JavaLanguageCharacteristics.getKeyword(keyword));
            }
        }
    }

    public final void Assert(JavaKeyword expectedKeyword) {
        assert getCurrentSymbol().getType() == JavaSymbolType.Keyword && getCurrentSymbol().getKeyword() != null
                && getCurrentSymbol().getKeyword() == expectedKeyword;
    }

    protected final boolean at(JavaKeyword keyword) {
        return at(JavaSymbolType.Keyword) && getCurrentSymbol().getKeyword() != null
                && getCurrentSymbol().getKeyword() == keyword;
    }

    protected final boolean acceptIf(JavaKeyword keyword) {
        if (at(keyword)) {
            acceptAndMoveNext();
            return true;
        }
        return false;
    }

    protected static Predicate<JavaSymbol> isSpacingToken(boolean includeNewLines, boolean includeComments) {
        return sym -> {

            return sym.getType() == JavaSymbolType.WhiteSpace
                    || (includeNewLines && sym.getType() == JavaSymbolType.NewLine)
                    || (includeComments && sym.getType() == JavaSymbolType.Comment);

        };
    }

    @Override
    public void parseBlock() {

        try (AutoCloseable disposable = pushSpanConfig((p) -> defaultSpanConfig(p))) {
            if (getContext() == null) {

                //throw new InvalidOperationException(RazorResources.getResource(RazorResources.Parser_Context_Not_Set());
            }

            // Unless changed, the block is a statement block
            try (AutoCloseable blockDispose = getContext().startBlock(BlockType.Statement)) {
                nextToken();
                acceptWhile(isSpacingToken(true, true));
                JavaSymbol current = getCurrentSymbol();
                if (at(JavaSymbolType.StringLiteral)
                        && getCurrentSymbol().getContent().length() > 0
                        && getCurrentSymbol().getContent().charAt(0) == SyntaxConstants.TransitionCharacter) {
                    Tuple<JavaSymbol, JavaSymbol> split = getLanguage().splitSymbol(getCurrentSymbol(), 1,
                            JavaSymbolType.Transition);
                    current = split.getItem1();
                    getContext().getSource().setPosition(split.getItem2().getStart().getAbsoluteIndex());
                    nextToken();
                } else if (at(JavaSymbolType.Transition)) {
                    nextToken();
                }

                // accept "@" if we see it, but if we don't, that's OK. We
                // assume we were started for a good reason
                if (current.getType() == JavaSymbolType.Transition) {
                    if (getSpan().getSymbols().size() > 0) {
                        output(SpanKind.Code);
                    }
                    atTransition(current);
                } else {
                    // No "@" -> Jump straight to afterTransition
                    afterTransition();
                }
                output(SpanKind.Code);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void defaultSpanConfig(SpanBuilder span) {
        span.setEditHandler(SpanEditHandler.createDefault((p) -> getLanguage().tokenizeString(p)));
        span.setCodeGenerator(new StatementCodeGenerator());
    }

    private void atTransition(JavaSymbol current) {
        assert current.getType() == JavaSymbolType.Transition;
        accept(current);
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);

        // output the "@" span and continue here
        output(SpanKind.Transition);
        afterTransition();
    }

    private void afterTransition() {


        try (AutoCloseable disposable = pushSpanConfig((p) -> defaultSpanConfig(p))) {
            ensureCurrent();
            try {
                // What type of block is this?
                if (!getEndOfFile()) {
                    if (getCurrentSymbol().getType() == JavaSymbolType.LeftParenthesis) {
                        getContext().getCurrentBlock().setType(BlockType.Expression);
                        getContext().getCurrentBlock().setCodeGenerator(new ExpressionCodeGenerator());
                        explicitExpression();
                        return;
                    } else if (getCurrentSymbol().getType() == JavaSymbolType.Identifier) {

                        Runnable handler = getDirectiveHandler(getCurrentSymbol().getContent());
                        //Consumer handler =getDirectiveHandler()
                        if (handler != null) {
                            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
                            handler.run();
                            return;
                        } else {
                            getContext().getCurrentBlock().setType(BlockType.Expression);
                            getContext().getCurrentBlock().setCodeGenerator(new ExpressionCodeGenerator());
                            implicitExpression();
                            return;
                        }
                    } else if (getCurrentSymbol().getType() == JavaSymbolType.Keyword) {
                        keywordBlock(true);
                        return;
                    } else if (getCurrentSymbol().getType() == JavaSymbolType.LeftBrace) {
                        verbatimBlock();
                        return;
                    }
                }

                // Invalid character
                getContext().getCurrentBlock().setType(BlockType.Expression);
                getContext().getCurrentBlock().setCodeGenerator(new ExpressionCodeGenerator());
                addMarkerSymbolIfNecessary();
                getSpan().setCodeGenerator(new ExpressionCodeGenerator());
                ImplicitExpressionEditHandler tempVar2 = new ImplicitExpressionEditHandler((p) -> getLanguage().tokenizeString(p),
                        DefaultKeywords, getIsNested());
                tempVar2.setAcceptedCharacters(AcceptedCharacters.NonWhiteSpace);
                getSpan().setEditHandler(tempVar2);
                if (at(JavaSymbolType.WhiteSpace) || at(JavaSymbolType.NewLine)) {
                    getContext().onError(getCurrentLocation().clone(),
                            RazorResources.getResource(RazorResources.ParseError_Unexpected_WhiteSpace_At_Start_Of_CodeBlock_CS));
                } else if (getEndOfFile()) {
                    getContext().onError(getCurrentLocation().clone(),
                            RazorResources.getResource(RazorResources.ParseError_Unexpected_EndOfFile_At_Start_Of_CodeBlock));
                } else {
                    getContext().onError(getCurrentLocation().clone(),
                            RazorResources.getResource(RazorResources.ParseError_Unexpected_Character_At_Start_Of_CodeBlock_CS),
                            getCurrentSymbol().getContent());
                }
            } finally {
                // Always put current character back in the buffer for the next
                // parser.
                putCurrentBack();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void verbatimBlock() {
        // assertSymbol(JavaSymbolType.LeftBrace);
        Block block = new Block(RazorResources.getResource(RazorResources.BlockName_Code), getCurrentLocation().clone());
        acceptAndMoveNext();

        // Set up the "{" span and output
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        output(SpanKind.MetaCode);

        // Set up auto-complete and parse the code block
        AutoCompleteEditHandler editHandler = new AutoCompleteEditHandler((p) -> getLanguage().tokenizeString(p));
        getSpan().setEditHandler(editHandler);
        codeBlock(false, block);

        getSpan().setCodeGenerator(new StatementCodeGenerator());
        addMarkerSymbolIfNecessary();
        if (!at(JavaSymbolType.RightBrace)) {
            editHandler.setAutoCompleteString("}");
        }
        output(SpanKind.Code);

        if (optional(JavaSymbolType.RightBrace)) {
            // Set up the "}" span
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        }

        if (!at(JavaSymbolType.WhiteSpace) && !at(JavaSymbolType.NewLine)) {
            putCurrentBack();
        }

        completeBlock(false);
        output(SpanKind.MetaCode);
    }

    private void implicitExpression() {
        getContext().getCurrentBlock().setType(BlockType.Expression);
        getContext().getCurrentBlock().setCodeGenerator(new ExpressionCodeGenerator());


        try (AutoCloseable disposable = pushSpanConfig(span -> {
            span.setEditHandler(
                    new ImplicitExpressionEditHandler(p -> getLanguage().tokenizeString(p), getKeywords(), getIsNested()));
            span.getEditHandler().setAcceptedCharacters(AcceptedCharacters.NonWhiteSpace);
            span.setCodeGenerator(new ExpressionCodeGenerator());
        })) {
            do {
                if (atIdentifier(true)) {
                    acceptAndMoveNext();
                }
            } while (methodCallOrArrayIndex());

            putCurrentBack();
            output(SpanKind.Code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean methodCallOrArrayIndex() {
        if (!getEndOfFile()) {
            if (getCurrentSymbol().getType() == JavaSymbolType.LeftParenthesis
                    || getCurrentSymbol().getType() == JavaSymbolType.LeftBracket) {
                // If we end within "(", whitespace is fine
                getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);

                JavaSymbolType right;
                boolean success = false;

                try (AutoCloseable disposable = pushSpanConfig((span, prev) -> {
                    prev.accept(span);
                    span.getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
                })) {
                    right = getLanguage().flipBracket(getCurrentSymbol().getType());
                    BalancingModes modes = BalancingModes.forValue(BalancingModes.BacktrackOnFailure.getValue()
                            | BalancingModes.AllowCommentsAndTemplates.getValue());
                    success = balance(modes);
                    if (!success) {
                        acceptUntil(JavaSymbolType.LessThan);
                    }
                    if (at(right)) {
                        acceptAndMoveNext();
                        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.NonWhiteSpace);
                    }
                    return methodCallOrArrayIndex();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (getCurrentSymbol().getType() == JavaSymbolType.Dot) {
                JavaSymbol dot = getCurrentSymbol();
                if (nextToken()) {
                    if (at(JavaSymbolType.Identifier) || at(JavaSymbolType.Keyword)) {
                        // accept the dot and return to the start
                        accept(dot);
                        return true; // continue
                    } else {
                        // Put the symbol back
                        putCurrentBack();
                    }
                }
                if (!getIsNested()) {
                    // Put the "." back
                    putBack(dot);
                } else {
                    accept(dot);
                }
            } else if (!at(JavaSymbolType.WhiteSpace) && !at(JavaSymbolType.NewLine)) {
                putCurrentBack();
            }
        }

        // Implicit Expression is complete
        return false;
    }

    private void completeBlock() {
        completeBlock(true);
    }

    private void completeBlock(boolean insertMarkerIfNecessary) {
        completeBlock(insertMarkerIfNecessary, insertMarkerIfNecessary);
    }

    private void completeBlock(boolean insertMarkerIfNecessary, boolean captureWhitespaceToEndOfLine) {
        if (insertMarkerIfNecessary && getContext().getLastAcceptedCharacters() != AcceptedCharacters.Any) {
            addMarkerSymbolIfNecessary();
        }

        ensureCurrent();

        // read whitespace, but not newlines
        // If we're not inserting a marker span, we don't need to capture
        // whitespace
        if (!getContext().getWhiteSpaceIsSignificantToAncestorBlock()
                && !getContext().getCurrentBlock().getType().equals(BlockType.Expression)
                && captureWhitespaceToEndOfLine && !getContext().getDesignTimeMode() && !getIsNested()) {
            captureWhitespaceAtEndOfCodeOnlyLine();
        } else {
            putCurrentBack();
        }
    }

    private void captureWhitespaceAtEndOfCodeOnlyLine() {

        // methods are not converted
        Iterable<JavaSymbol> ws = readWhile(sym -> sym.getType() == JavaSymbolType.WhiteSpace);
        if (at(JavaSymbolType.NewLine)) {
            accept(ws);
            acceptAndMoveNext();
            putCurrentBack();
        } else {
            putCurrentBack();
            putBack(ws);
        }
    }

    private void configureExplicitExpressionSpan(SpanBuilder sb) {
        sb.setEditHandler(SpanEditHandler.createDefault((p) -> getLanguage().tokenizeString(p)));
        sb.setCodeGenerator(new ExpressionCodeGenerator());
    }

    private void explicitExpression() {
        Block block = new Block(RazorResources.getResource(RazorResources.BlockName_ExplicitExpression),
                getCurrentLocation().clone());
        assertSymbol(JavaSymbolType.LeftParenthesis);
        acceptAndMoveNext();
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        output(SpanKind.MetaCode);


        try (AutoCloseable disposable = pushSpanConfig((p) -> configureExplicitExpressionSpan(p))) {
            BalancingModes modes = BalancingModes.forValue(BalancingModes.BacktrackOnFailure.getValue()
                    | BalancingModes.NoErrorOnFailure.getValue() | BalancingModes.AllowCommentsAndTemplates.getValue());
            boolean success = balance(modes, JavaSymbolType.LeftParenthesis, JavaSymbolType.RightParenthesis,
                    block.getStart().clone());

            if (!success) {
                acceptUntil(JavaSymbolType.LessThan);
                getContext().onError(block.getStart().clone(),
                        RazorResources.getResource(RazorResources.ParseError_Expected_EndOfBlock_Before_EOF),
                        block.getName(), ")", "(");
            }

            // If necessary, put an empty-content marker symbol here
            if (getSpan().getSymbols().size() == 0) {
                try {
                    accept(new JavaSymbol(getCurrentLocation().clone(), "", JavaSymbolType.Unknown));
                } catch (ArgumentNullException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            // output the content span and then capture the ")"
            output(SpanKind.Code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        optional(JavaSymbolType.RightParenthesis);
        if (!getEndOfFile()) {
            putCurrentBack();
        }
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        completeBlock(false);
        output(SpanKind.MetaCode);
    }

    private void template() {
        if (getContext().isWithin(BlockType.Template)) {
            getContext().onError(getCurrentLocation().clone(),
                    RazorResources.getResource(RazorResources.ParseError_InlineMarkup_Blocks_Cannot_Be_Nested));
        }
        output(SpanKind.Code);

        try (AutoCloseable disposable = getContext().startBlock(BlockType.Template)) {
            getContext().getCurrentBlock().setCodeGenerator(new TemplateBlockCodeGenerator());
            putCurrentBack();
            otherParserBlock();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void otherParserBlock() {
        parseWithOtherParser(p -> {
            try {
                p.parseBlock();
            } catch (InvalidOperationException e) {
                e.printStackTrace();
            }
        });
    }

    private void sectionBlock(String left, String right, boolean caseSensitive) {

        // methods are not converted
        parseWithOtherParser(p -> {
            try {
                p.parseSection(Tuple.create(left, right), caseSensitive);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void nestedBlock() {
        output(SpanKind.Code);
        boolean wasNested = getIsNested();
        setIsNested(true);


        try (AutoCloseable disposable = pushSpanConfig()) {
            parseBlock();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initialize(getSpan());
        setIsNested(wasNested);
        nextToken();
    }

    @Override
    protected boolean isAtEmbeddedTransition(boolean allowTemplatesAndComments, boolean allowTransitions) {

        return allowTemplatesAndComments && ((getLanguage().isTransition(getCurrentSymbol())
                && nextIs(JavaSymbolType.LessThan, JavaSymbolType.Colon))
                || getLanguage().isCommentStart(getCurrentSymbol()));
    }

    @Override
    protected void handleEmbeddedTransition() {
        if (getLanguage().isTransition(getCurrentSymbol())) {
            putCurrentBack();
            template();
        } else if (getLanguage().isCommentStart(getCurrentSymbol())) {
            razorComment();
        }
    }

    private void parseWithOtherParser(Consumer<ParserBase> parseAction) {

        try (AutoCloseable disposable = pushSpanConfig()) {
            getContext().switchActiveParser();
            parseAction.accept(getContext().getMarkupParser());
            getContext().switchActiveParser();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initialize(getSpan());
        nextToken();
    }

    @Override
    public boolean equals(Object obj, Object others) {
        // TODO Auto-generated method stub
        return obj.equals(others);
    }


}
