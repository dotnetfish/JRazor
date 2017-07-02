package com.superstudio.language.java.parser;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.Tuple;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.csharpbridge.action.Action;
import com.superstudio.commons.csharpbridge.action.Func2;
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
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


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
        AcceptAndMoveNext();
        getContext().getCurrentBlock().setType(BlockType.Directive);

        // accept spaces, but not newlines
        boolean foundSomeWhitespace = At(JavaSymbolType.WhiteSpace);
        AcceptWhile(JavaSymbolType.WhiteSpace);
        Output(SpanKind.MetaCode, foundSomeWhitespace ? AcceptedCharacters.None : AcceptedCharacters.Any);

        // First non-whitespace character starts the Layout Page, then newline
        // ends it
        AcceptUntil(JavaSymbolType.NewLine);
        getSpan().setCodeGenerator(new SetLayoutCodeGenerator(getSpan().getContent().toString()));
        EditorHints hints = EditorHints.forValue(EditorHints.LayoutPage.getValue() | EditorHints.VirtualPath.getValue());
        getSpan().getEditHandler().setEditorHints(hints);
        boolean foundNewline = Optional(JavaSymbolType.NewLine);
        AddMarkerSymbolIfNecessary();
        Output(SpanKind.MetaCode, foundNewline ? AcceptedCharacters.None : AcceptedCharacters.Any);
    }

    protected void sessionStateDirective() {
        assertDirective(SyntaxConstants.Java.SessionStateKeyword);
        AcceptAndMoveNext();

        sessionStateDirectiveCore();
    }

    protected final void sessionStateDirectiveCore() {

        sessionStateTypeDirective(RazorResources.getResource(RazorResources.ParserEror_SessionDirectiveMissingValue),
                (key, value) -> new RazorDirectiveAttributeCodeGenerator(key, value));
    }

    protected final void sessionStateTypeDirective(String noValueError,
                                                   Func2<String, String, SpanCodeGenerator> createCodeGenerator) {
        // Set the block type
        getContext().getCurrentBlock().setType(BlockType.Directive);

        // accept whitespace
        JavaSymbol remainingWs = AcceptSingleWhiteSpaceCharacter();

        if (getSpan().getSymbols().size() > 1) {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }

        Output(SpanKind.MetaCode);

        if (remainingWs != null) {
            Accept(remainingWs);
        }
        AcceptWhile(isSpacingToken(false, true));

        // parse a Type Name
        if (!validSessionStateValue()) {
            getContext().OnError(getCurrentLocation().clone(), noValueError);
        }

        // Pull out the type name

        String sessionStateValue=(getSpan().getSymbols().stream().collect(
                StringBuilder::new,(StringBuilder builder,ISymbol sym)->builder.append(sym.getContent()),StringBuilder::append
        )).toString();

        /*String sessionStateValue = StringHelper
                .concat(
                        CollectionHelper.select(getSpan().getSymbols(),
                                sym -> sym.getContent())).trim();
*/
        // Set up code generation
        getSpan().setCodeGenerator(createCodeGenerator.execute(SyntaxConstants.Java.SessionStateKeyword, sessionStateValue));

        // Output the span and finish the block
        completeBlock();
        Output(SpanKind.Code);
    }

    protected boolean validSessionStateValue() {
        return Optional(JavaSymbolType.Identifier);
    }


    protected void helperDirective() {
        boolean nested = getContext().IsWithin(BlockType.Helper);

        // Set the block and span type
        getContext().getCurrentBlock().setType(BlockType.Helper);

        // Verify we're on "helper" and accept
        assertDirective(SyntaxConstants.Java.HelperKeyword);
        Block block = new Block(getCurrentSymbol().getContent().toString().toLowerCase(), getCurrentLocation().clone());
        AcceptAndMoveNext();

        if (nested) {
            getContext().OnError(getCurrentLocation().clone(), RazorResources.getResource(RazorResources.ParseError_Helpers_Cannot_Be_Nested));
        }

        // accept a single whitespace character if present, if not, we should
        // stop now
        if (!At(JavaSymbolType.WhiteSpace)) {
            String error;
            if (At(JavaSymbolType.NewLine)) {
                error = RazorResources.getResource(RazorResources.ErrorComponent_Newline);
            } else if (getEndOfFile()) {
                error = RazorResources.getResource(RazorResources.ErrorComponent_EndOfFile);
            } else {
                error = String.format(RazorResources.getResource(RazorResources.ErrorComponent_Character), getCurrentSymbol().getContent());
            }

            getContext().OnError(getCurrentLocation().clone(),
                    RazorResources.getResource(RazorResources.ParseError_Unexpected_Character_At_Helper_Name_Start), error);
            putCurrentBack();
            Output(SpanKind.MetaCode);
            return;
        }

        JavaSymbol remainingWs = AcceptSingleWhiteSpaceCharacter();

        // Output metacode and continue
        Output(SpanKind.MetaCode);
        if (remainingWs != null) {
            Accept(remainingWs);
        }
        AcceptWhile(isSpacingToken(false, true)); // Don't accept newlines.

        // Expecting an identifier (helper name)
        boolean errorReported = !Required(JavaSymbolType.Identifier, true,
                RazorResources.getResource(RazorResources.ParseError_Unexpected_Character_At_Helper_Name_Start));
        if (!errorReported) {
            Assert(JavaSymbolType.Identifier);
            AcceptAndMoveNext();
        }

        AcceptWhile(isSpacingToken(false, true));

        // Expecting parameter list start: "("
        SourceLocation bracketErrorPos = getCurrentLocation().clone();
        if (!Optional(JavaSymbolType.LeftParenthesis)) {
            if (!errorReported) {
                errorReported = true;
                getContext().OnError(getCurrentLocation().clone(),
                        RazorResources.getResource(RazorResources.ParseError_MissingCharAfterHelperName), "(");
            }
        } else {
            SourceLocation bracketStart = getCurrentLocation().clone();
            if (!balance(BalancingModes.NoErrorOnFailure, JavaSymbolType.LeftParenthesis,
                    JavaSymbolType.RightParenthesis, bracketStart)) {
                errorReported = true;
                getContext().OnError(bracketErrorPos, RazorResources.getResource(RazorResources.ParseError_UnterminatedHelperParameterList));
            }
            Optional(JavaSymbolType.RightParenthesis);
        }

        int bookmark = getCurrentLocation().getAbsoluteIndex();
        Iterable<JavaSymbol> ws = ReadWhile(isSpacingToken(true, true));

        // Expecting a "{"
        SourceLocation errorLocation = getCurrentLocation().clone();
        boolean headerComplete = At(JavaSymbolType.LeftBrace);
        if (headerComplete) {
            Accept(ws);
            AcceptAndMoveNext();
        } else {
            getContext().getSource().setPosition(bookmark);
            nextToken();
            AcceptWhile(isSpacingToken(false, true));
            if (!errorReported) {
                getContext().OnError(errorLocation, RazorResources.getResource(RazorResources.ParseError_MissingCharAfterHelperParameters),
                        getLanguage().getSample(JavaSymbolType.LeftBrace));
            }
        }

        // Grab the signature and build the code generator
        AddMarkerSymbolIfNecessary();
        LocationTagged<String> signature = getSpan().getContent();
        HelperCodeGenerator blockGen = new HelperCodeGenerator(signature, headerComplete);
        getContext().getCurrentBlock().setCodeGenerator(blockGen);

        // The block will generate appropriate code,
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);

        if (!headerComplete) {
            completeBlock();
            Output(SpanKind.Code);
            return;
        } else {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
            Output(SpanKind.Code);
        }

        AutoCompleteEditHandler bodyEditHandler =
                new AutoCompleteEditHandler(p -> getLanguage().tokenizeString(p));

        try (AutoCloseable disposable = PushSpanConfig((p) -> defaultSpanConfig(p))) {

            try (AutoCloseable blockDispose = getContext().startBlock(BlockType.Statement)) {
                getSpan().setEditHandler(bodyEditHandler);
                codeBlock(false, block);
                completeBlock(true);
                Output(SpanKind.Code);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initialize(getSpan());

        EnsureCurrent();

        getSpan().setCodeGenerator(SpanCodeGenerator.Null); // The block will
        // generate the
        // footer code.
        if (!Optional(JavaSymbolType.RightBrace)) {
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
        Output(SpanKind.Code);
    }

    protected void sectionDirective() {
        boolean nested = getContext().IsWithin(BlockType.Section);
        boolean errorReported = false;

        // Set the block and span type
        getContext().getCurrentBlock().setType(BlockType.Section);

        // Verify we're on "section" and accept
        assertDirective(SyntaxConstants.Java.SectionKeyword);
        AcceptAndMoveNext();

        if (nested) {
            getContext().OnError(getCurrentLocation().clone(), String.format(
                    RazorResources.getResource(RazorResources.ParseError_Sections_Cannot_Be_Nested), RazorResources.getResource(RazorResources.SectionExample_CS)));
            errorReported = true;
        }

        Iterable<JavaSymbol> ws = ReadWhile(isSpacingToken(true, false));

        // Get the section name
        String sectionName = "";
        if (!Required(JavaSymbolType.Identifier, true,
                RazorResources.getResource(RazorResources.ParseError_Unexpected_Character_At_Section_Name_Start))) {
            if (!errorReported) {
                errorReported = true;
            }

            putCurrentBack();
            putBack(ws);
            AcceptWhile(isSpacingToken(false, false));
        } else {
            Accept(ws);
            sectionName = getCurrentSymbol().getContent();
            AcceptAndMoveNext();
        }
        getContext().getCurrentBlock().setCodeGenerator(new SectionCodeGenerator(sectionName));

        SourceLocation errorLocation = getCurrentLocation().clone();
        ws = ReadWhile(isSpacingToken(true, false));

        // Get the starting brace
        boolean sawStartingBrace = At(JavaSymbolType.LeftBrace);
        if (!sawStartingBrace) {
            if (!errorReported) {
                errorReported = true;
                getContext().OnError(errorLocation, RazorResources.getResource(RazorResources.ParseError_MissingOpenBraceAfterSection));
            }

            putCurrentBack();
            putBack(ws);
            AcceptWhile(isSpacingToken(false, false));
            Optional(JavaSymbolType.NewLine);
            Output(SpanKind.MetaCode);
            completeBlock();
            return;
        } else {
            Accept(ws);
        }

        // Set up edit handler
        AutoCompleteEditHandler tempVar = new AutoCompleteEditHandler(p -> getLanguage().tokenizeString(p));
        tempVar.setAutoCompleteAtEndOfSpan(true);
        AutoCompleteEditHandler editHandler = tempVar;

        getSpan().setEditHandler(editHandler);
        getSpan().accept(getCurrentSymbol());

        // Output Metacode then switch to section parser
        Output(SpanKind.MetaCode);
        sectionBlock("{", "}", true);

        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        // Check for the terminating "}"
        if (!Optional(JavaSymbolType.RightBrace)) {
            editHandler.setAutoCompleteString("}");
            getContext().OnError(getCurrentLocation().clone(), RazorResources.getResource(RazorResources.ParseError_Expected_X),
                    getLanguage().getSample(JavaSymbolType.RightBrace));
        } else {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }
        completeBlock(false, true);
        Output(SpanKind.MetaCode);
        return;
    }

    protected void functionsDirective() {
        // Set the block type
        getContext().getCurrentBlock().setType(BlockType.Functions);

        // Verify we're on "functions" and accept
        assertDirective(SyntaxConstants.Java.FunctionsKeyword);
        Block block = new Block(getCurrentSymbol());
        AcceptAndMoveNext();

        AcceptWhile(isSpacingToken(true, false));

        if (!At(JavaSymbolType.LeftBrace)) {
            getContext().OnError(getCurrentLocation().clone(), RazorResources.getResource(RazorResources.ParseError_Expected_X),
                    getLanguage().getSample(JavaSymbolType.LeftBrace));
            completeBlock();
            Output(SpanKind.MetaCode);
            return;
        } else {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }

        // Capture start point and continue
        SourceLocation blockStart = getCurrentLocation().clone();
        AcceptAndMoveNext();

        // Output what we've seen and continue
        Output(SpanKind.MetaCode);

        AutoCompleteEditHandler editHandler = new AutoCompleteEditHandler(p -> getLanguage().tokenizeString(p));
        getSpan().setEditHandler(editHandler);

        balance(BalancingModes.NoErrorOnFailure, JavaSymbolType.LeftBrace, JavaSymbolType.RightBrace, blockStart);
        getSpan().setCodeGenerator(new TypeMemberCodeGenerator());
        if (!At(JavaSymbolType.RightBrace)) {
            editHandler.setAutoCompleteString("}");
            getContext().OnError(block.getStart().clone(),
                    RazorResources.getResource(RazorResources.ParseError_Expected_CloseBracket_Before_EOF), block.getName(), "}", "{");
            completeBlock();
            Output(SpanKind.Code);
        } else {
            Output(SpanKind.Code);
            Assert(JavaSymbolType.RightBrace);
            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
            AcceptAndMoveNext();
            completeBlock();
            Output(SpanKind.MetaCode);
        }
    }

    protected void inheritsDirective() {
        // Verify we're on the right keyword and accept
        assertDirective(SyntaxConstants.Java.InheritsKeyword);
        AcceptAndMoveNext();

        inheritsDirectiveCore();
    }


    protected final void assertDirective(String directive) {
        Assert(JavaSymbolType.Identifier);
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
        JavaSymbol remainingWs = AcceptSingleWhiteSpaceCharacter();

        if (getSpan().getSymbols().size() > 1) {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }

        Output(SpanKind.MetaCode);

        if (remainingWs != null) {
            Accept(remainingWs);
        }
        AcceptWhile(isSpacingToken(false, true));

        if (getEndOfFile() || At(JavaSymbolType.WhiteSpace) || At(JavaSymbolType.NewLine)) {
            getContext().OnError(getCurrentLocation().clone(), noTypeNameError);
        }

        // parse to the end of the line
        AcceptUntil(JavaSymbolType.NewLine);
        if (!getContext().getDesignTimeMode()) {
            // We want the newline to be treated as code, but it causes issues
            // at design-time.
            Optional(JavaSymbolType.NewLine);
        }

        // Pull out the type name
        String baseType = getSpan().getContent().toString();

        // Set up code generation
        getSpan().setCodeGenerator(createCodeGenerator.apply(baseType.trim()));

        // Output the span and finish the block
        completeBlock();
        Output(SpanKind.Code);
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
        getContext().OnError(getCurrentLocation().clone(),
                String.format(RazorResources.getResource(RazorResources.ParseError_ReservedWord), getCurrentSymbol().getContent()));
        AcceptAndMoveNext();
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        getContext().getCurrentBlock().setType(BlockType.Directive);
        completeBlock();
        Output(SpanKind.MetaCode);
    }

    private void keywordBlock(boolean topLevel) {
        handleKeyword(topLevel, () -> {
            getContext().getCurrentBlock().setType(BlockType.Expression);
            getContext().getCurrentBlock().setCodeGenerator(new ExpressionCodeGenerator());
            implicitExpression();
        });
    }

    private void caseStatement(boolean topLevel) {
        Assert(JavaSymbolType.Keyword);
        assert getCurrentSymbol().getKeyword() != null && (getCurrentSymbol().getKeyword() == JavaKeyword.Case
                || getCurrentSymbol().getKeyword() == JavaKeyword.Default);
        AcceptUntil(JavaSymbolType.Colon);
        Optional(JavaSymbolType.Colon);
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
            Accept(ws);
            Assert(JavaKeyword.While);
            AcceptAndMoveNext();
            AcceptWhile(isSpacingToken(true, true));
            if (acceptCondition() && Optional(JavaSymbolType.Semicolon)) {
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
        AcceptAndMoveNext();
        AcceptWhile(isSpacingToken(false, true));

        if (At(JavaSymbolType.LeftParenthesis)) {
            // try ( =-> try statement
            usingStatement(block);
        } else if (At(JavaSymbolType.Identifier)) {
            //try {  -> try{} catch..
            tryStatement(topLevel);
            /*if (!topLevel) {
				getContext().OnError(block.getStart().clone(),
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
        AcceptWhile(isSpacingToken(false, true));
        if (Required(JavaSymbolType.Identifier, true, "import 关键字不正确")) {
            // Set block type to directive
            if (!isTopLevel) {//import should be topLevel @import
                getContext().OnError(block.getStart().clone(),
                        RazorResources.getResource(RazorResources.ParseError_NamespaceImportAndTypeAlias_Cannot_Exist_Within_CodeBlock));
                standardStatement();

            } else {
                getContext().getCurrentBlock().setType(BlockType.Directive);

                // parse a type name
                Assert(JavaSymbolType.Identifier);
                namespaceOrTypeName();
                Iterable<JavaSymbol> ws = ReadWhile(isSpacingToken(true, true));
                if (At(JavaSymbolType.Assign)) {
                    // Alias
                    Accept(ws);
                    Assert(JavaSymbolType.Assign);
                    AcceptAndMoveNext();

                    AcceptWhile(isSpacingToken(true, true));

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

                // Optional ";"
                if (EnsureCurrent()) {
                    Optional(JavaSymbolType.Semicolon);
                }
            }
            if (isTopLevel) {
                completeBlock();
            }
        }


    }

    private boolean namespaceOrTypeName() {
        if (Optional(JavaSymbolType.Identifier) || Optional(JavaSymbolType.Keyword) || Optional(JavaSymbolType.Star)) {
            //Optional(JavaSymbolType.Star); // .*
            if (Optional(JavaSymbolType.DoubleColon)) {
                if (!Optional(JavaSymbolType.Identifier)) {
                    Optional(JavaSymbolType.Keyword);
                }
            }
            //in java not <>
			/*if (at(JavaSymbolType.LessThan)) {
				typeArgumentList();
			}*/
            if (Optional(JavaSymbolType.Dot)) {
                namespaceOrTypeName();
            }
			/*while (at(JavaSymbolType.LeftBracket)) {
				balance(BalancingModes.None);
				Optional(JavaSymbolType.RightBracket);
			}*/
            return true;
        } else {
            return false;
        }
    }

    private void typeArgumentList() {
        Assert(JavaSymbolType.LessThan);
        balance(BalancingModes.None);
        Optional(JavaSymbolType.GreaterThan);
    }

    private void usingStatement(Block block) {
        Assert(JavaSymbolType.LeftParenthesis);

        // parse condition
        if (acceptCondition()) {
            AcceptWhile(isSpacingToken(true, true));

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
            Accept(ws);
            Assert(JavaKeyword.Catch);
            conditionalBlock(false);
            afterTryClause();
        } else if (at(JavaKeyword.Finally)) {
            Accept(ws);
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
            Accept(ws);
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

        AcceptAndMoveNext();
        AcceptWhile(isSpacingToken(true, true));
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
            if (!At(JavaSymbolType.LeftBrace)) {
                getContext().OnError(getCurrentLocation().clone(),
                        RazorResources.getResource(RazorResources.ParseError_SingleLine_ControlFlowStatements_Not_Allowed),
                        getLanguage().getSample(JavaSymbolType.LeftBrace), getCurrentSymbol().getContent());
            }

            // parse the statement and then we're done
            statement(block);
        }
    }

    private void unconditionalBlock() {
        Assert(JavaSymbolType.Keyword);
        Block block = new Block(getCurrentSymbol());
        AcceptAndMoveNext();
        AcceptWhile(isSpacingToken(true, true));
        expectCodeBlock(block);
    }

    private void conditionalBlock(boolean topLevel) {
        Assert(JavaSymbolType.Keyword);
        Block block = new Block(getCurrentSymbol());
        conditionalBlock(block);
        if (topLevel) {
            completeBlock();
        }
    }

    private void conditionalBlock(Block block) {
        AcceptAndMoveNext();
        AcceptWhile(isSpacingToken(true, true));

        // parse the condition, if present (if not present, we'll let the C#
        // compiler complain)
        if (acceptCondition()) {
            AcceptWhile(isSpacingToken(true, true));
            expectCodeBlock(block);
        }
    }

    private boolean acceptCondition() {
        if (At(JavaSymbolType.LeftParenthesis)) {
            BalancingModes modes = BalancingModes.forValue(BalancingModes.BacktrackOnFailure.getValue() | BalancingModes.AllowCommentsAndTemplates.getValue());
            boolean complete = balance(modes);
            if (!complete) {
                AcceptUntil(JavaSymbolType.NewLine);
            } else {
                Optional(JavaSymbolType.RightParenthesis);
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
        JavaSymbol lastWs = AcceptWhiteSpaceInLines();
        assert lastWs == null || (lastWs.getStart().getAbsoluteIndex()
                + lastWs.getContent().length() == getCurrentLocation().getAbsoluteIndex());

        if (getEndOfFile()) {
            if (lastWs != null) {
                Accept(lastWs);
            }
            return;
        }

        JavaSymbolType type = getCurrentSymbol().getType();
        SourceLocation loc = getCurrentLocation().clone();

        boolean isSingleLineMarkup = type == JavaSymbolType.Transition && NextIs(JavaSymbolType.Colon);
        boolean isMarkup = isSingleLineMarkup || type == JavaSymbolType.LessThan
                || (type == JavaSymbolType.Transition && NextIs(JavaSymbolType.LessThan));

        if (getContext().getDesignTimeMode() || !isMarkup) {
            // CODE owns whitespace, MARKUP owns it ONLY in DesignTimeMode.
            if (lastWs != null) {
                Accept(lastWs);
            }
        } else {
            // MARKUP owns whitespace EXCEPT in DesignTimeMode.
            putCurrentBack();
            putBack(lastWs);
        }

        if (isMarkup) {
            if (type == JavaSymbolType.Transition && !isSingleLineMarkup) {
                getContext().OnError(loc,
                        RazorResources.getResource(RazorResources.ParseError_AtInCode_Must_Be_Followed_By_Colon_Paren_Or_Identifier_Start));
            }

            // Markup block
            Output(SpanKind.Code);
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
                Output(SpanKind.Code);
                RazorComment();
                statement(block);
                break;
            case LeftBrace:
                // Verbatim Block
                block = (block != null) ? block
                        : new Block(RazorResources.getResource(RazorResources.BlockName_Code), getCurrentLocation().clone());
                AcceptAndMoveNext();
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
                AcceptAndMoveNext();
                break;
            default:
                // Other statement
                standardStatement();
                break;
        }
    }

    private void embeddedExpression() {
        // First, verify the type of the block
        Assert(JavaSymbolType.Transition);
        JavaSymbol transition = getCurrentSymbol();
        nextToken();

        if (At(JavaSymbolType.Transition)) {
            // Escaped "@"
            Output(SpanKind.Code);

            // Output "@" as hidden span
            Accept(transition);
            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
            Output(SpanKind.Code);

            Assert(JavaSymbolType.Transition);
            AcceptAndMoveNext();
            standardStatement();
        } else {
            // Throw errors as necessary, but continue parsing
            if (At(JavaSymbolType.Keyword)) {
                getContext().OnError(getCurrentLocation().clone(),
                        RazorResources.getResource(RazorResources.ParseError_Unexpected_Keyword_After_At),
                        JavaLanguageCharacteristics.getKeyword(getCurrentSymbol().getKeyword()));
            } else if (At(JavaSymbolType.LeftBrace)) {
                getContext().OnError(getCurrentLocation().clone(),
                        RazorResources.getResource(RazorResources.ParseError_Unexpected_Nested_CodeBlock));
            }

            // @( or @foo - Nested expression, parse a child block
            putCurrentBack();
            putBack(transition);

            // Before exiting, add a marker span if necessary
            AddMarkerSymbolIfNecessary();

            nestedBlock();
        }
    }

    private void standardStatement() {
        while (!getEndOfFile()) {
            int bookmark = getCurrentLocation().getAbsoluteIndex();

            // methods are not converted
            Iterable<JavaSymbol> read = ReadWhile(sym -> sym.getType() != JavaSymbolType.Semicolon
                    && sym.getType() != JavaSymbolType.RazorCommentTransition
                    && sym.getType() != JavaSymbolType.Transition && sym.getType() != JavaSymbolType.LeftBrace
                    && sym.getType() != JavaSymbolType.LeftParenthesis
                    && sym.getType() != JavaSymbolType.LeftBracket && sym.getType() != JavaSymbolType.RightBrace);
            if (At(JavaSymbolType.LeftBrace) || At(JavaSymbolType.LeftParenthesis)
                    || At(JavaSymbolType.LeftBracket)) {
                Accept(read);
                BalancingModes modes = BalancingModes.forValue(BalancingModes.AllowCommentsAndTemplates.getValue()
                        | BalancingModes.BacktrackOnFailure.getValue());
                if (balance(modes)) {
                    Optional(JavaSymbolType.RightBrace);
                } else {
                    // Recovery
                    AcceptUntil(JavaSymbolType.LessThan, JavaSymbolType.RightBrace);
                    return;
                }
            } else if (At(JavaSymbolType.Transition) && (NextIs(JavaSymbolType.LessThan, JavaSymbolType.Colon))) {
                Accept(read);
                Output(SpanKind.Code);
                template();
            } else if (At(JavaSymbolType.RazorCommentTransition)) {
                Accept(read);
                RazorComment();
            } else if (At(JavaSymbolType.Semicolon)) {
                Accept(read);
                AcceptAndMoveNext();
                return;
            } else if (At(JavaSymbolType.RightBrace)) {
                Accept(read);
                return;
            } else {
                getContext().getSource().setPosition(bookmark);
                nextToken();
                AcceptUntil(JavaSymbolType.LessThan, JavaSymbolType.RightBrace);
                return;
            }
        }
    }

    private void codeBlock(Block block) {
        codeBlock(true, block);
    }

    private void codeBlock(boolean acceptTerminatingBrace, Block block) {
        EnsureCurrent();
        while (!getEndOfFile() && !At(JavaSymbolType.RightBrace)) {
            // parse a statement, then return here
            statement();
            EnsureCurrent();
        }

        if (getEndOfFile()) {
            getContext().OnError(block.getStart().clone(),
                    RazorResources.getResource(RazorResources.ParseError_Expected_CloseBracket_Before_EOF), block.getName(), '}', '{');
        } else if (acceptTerminatingBrace) {
            Assert(JavaSymbolType.RightBrace);
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
            AcceptAndMoveNext();
        }
    }

    private void handleKeyword(boolean topLevel, Action fallback) {
        assert getCurrentSymbol().getType() == JavaSymbolType.Keyword && getCurrentSymbol().getKeyword() != null;
        Consumer<Boolean> handler = null;
        JavaKeyword keyword = getCurrentSymbol().getKeyword();


        if ((handler = _keywordParsers.get(keyword)) != null) {

            handler.accept(topLevel);
        } else {

            fallback.execute();
        }

    }

    private Iterable<JavaSymbol> skipToNextImportantToken() {
        while (!getEndOfFile()) {
            Iterable<JavaSymbol> ws = ReadWhile(isSpacingToken(true, true));
            if (At(JavaSymbolType.RazorCommentTransition)) {
                Accept(ws);
                getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
                RazorComment();
            } else {
                return ws;
            }
        }
        return Collections.emptyList();
    }


    protected void outputSpanBeforeRazorComment() {
        AddMarkerSymbolIfNecessary();
        Output(SpanKind.Code);
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

    private java.util.HashMap<String, Action> _directiveParsers = new java.util.HashMap<String, Action>();
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

    protected final void mapDirectives(Action handler, String... directives) {
        for (String directive : directives) {
            _directiveParsers.put(directive, handler);
            getKeywords().add(directive);
        }
    }

    protected final Action getDirectiveHandler(String directive) {
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
        return At(JavaSymbolType.Keyword) && getCurrentSymbol().getKeyword() != null
                && getCurrentSymbol().getKeyword() == keyword;
    }

    protected final boolean acceptIf(JavaKeyword keyword) {
        if (at(keyword)) {
            AcceptAndMoveNext();
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

        try (AutoCloseable disposable = PushSpanConfig((p) -> defaultSpanConfig(p))) {
            if (getContext() == null) {

                //throw new InvalidOperationException(RazorResources.getResource(RazorResources.Parser_Context_Not_Set());
            }

            // Unless changed, the block is a statement block
            try (AutoCloseable blockDispose = getContext().startBlock(BlockType.Statement)) {
                nextToken();
                AcceptWhile(isSpacingToken(true, true));
                JavaSymbol current = getCurrentSymbol();
                if (At(JavaSymbolType.StringLiteral)
                        && getCurrentSymbol().getContent().length() > 0
                        && getCurrentSymbol().getContent().charAt(0) == SyntaxConstants.TransitionCharacter) {
                    Tuple<JavaSymbol, JavaSymbol> split = getLanguage().splitSymbol(getCurrentSymbol(), 1,
                            JavaSymbolType.Transition);
                    current = split.getItem1();
                    getContext().getSource().setPosition(split.getItem2().getStart().getAbsoluteIndex());
                    nextToken();
                } else if (At(JavaSymbolType.Transition)) {
                    nextToken();
                }

                // accept "@" if we see it, but if we don't, that's OK. We
                // assume we were started for a good reason
                if (current.getType() == JavaSymbolType.Transition) {
                    if (getSpan().getSymbols().size() > 0) {
                        Output(SpanKind.Code);
                    }
                    atTransition(current);
                } else {
                    // No "@" -> Jump straight to afterTransition
                    afterTransition();
                }
                Output(SpanKind.Code);
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
        Accept(current);
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);

        // Output the "@" span and continue here
        Output(SpanKind.Transition);
        afterTransition();
    }

    private void afterTransition() {


        try (AutoCloseable disposable = PushSpanConfig((p) -> defaultSpanConfig(p))) {
            EnsureCurrent();
            try {
                // What type of block is this?
                if (!getEndOfFile()) {
                    if (getCurrentSymbol().getType() == JavaSymbolType.LeftParenthesis) {
                        getContext().getCurrentBlock().setType(BlockType.Expression);
                        getContext().getCurrentBlock().setCodeGenerator(new ExpressionCodeGenerator());
                        explicitExpression();
                        return;
                    } else if (getCurrentSymbol().getType() == JavaSymbolType.Identifier) {
                        Action handler = getDirectiveHandler(getCurrentSymbol().getContent());
                        if (handler != null) {
                            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
                            handler.execute();
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
                AddMarkerSymbolIfNecessary();
                getSpan().setCodeGenerator(new ExpressionCodeGenerator());
                ImplicitExpressionEditHandler tempVar2 = new ImplicitExpressionEditHandler((p) -> getLanguage().tokenizeString(p),
                        DefaultKeywords, getIsNested());
                tempVar2.setAcceptedCharacters(AcceptedCharacters.NonWhiteSpace);
                getSpan().setEditHandler(tempVar2);
                if (At(JavaSymbolType.WhiteSpace) || At(JavaSymbolType.NewLine)) {
                    getContext().OnError(getCurrentLocation().clone(),
                            RazorResources.getResource(RazorResources.ParseError_Unexpected_WhiteSpace_At_Start_Of_CodeBlock_CS));
                } else if (getEndOfFile()) {
                    getContext().OnError(getCurrentLocation().clone(),
                            RazorResources.getResource(RazorResources.ParseError_Unexpected_EndOfFile_At_Start_Of_CodeBlock));
                } else {
                    getContext().OnError(getCurrentLocation().clone(),
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
        // Assert(JavaSymbolType.LeftBrace);
        Block block = new Block(RazorResources.getResource(RazorResources.BlockName_Code), getCurrentLocation().clone());
        AcceptAndMoveNext();

        // Set up the "{" span and output
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        Output(SpanKind.MetaCode);

        // Set up auto-complete and parse the code block
        AutoCompleteEditHandler editHandler = new AutoCompleteEditHandler((p) -> getLanguage().tokenizeString(p));
        getSpan().setEditHandler(editHandler);
        codeBlock(false, block);

        getSpan().setCodeGenerator(new StatementCodeGenerator());
        AddMarkerSymbolIfNecessary();
        if (!At(JavaSymbolType.RightBrace)) {
            editHandler.setAutoCompleteString("}");
        }
        Output(SpanKind.Code);

        if (Optional(JavaSymbolType.RightBrace)) {
            // Set up the "}" span
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        }

        if (!At(JavaSymbolType.WhiteSpace) && !At(JavaSymbolType.NewLine)) {
            putCurrentBack();
        }

        completeBlock(false);
        Output(SpanKind.MetaCode);
    }

    private void implicitExpression() {
        getContext().getCurrentBlock().setType(BlockType.Expression);
        getContext().getCurrentBlock().setCodeGenerator(new ExpressionCodeGenerator());


        try (AutoCloseable disposable = PushSpanConfig(span -> {
            span.setEditHandler(
                    new ImplicitExpressionEditHandler(p -> getLanguage().tokenizeString(p), getKeywords(), getIsNested()));
            span.getEditHandler().setAcceptedCharacters(AcceptedCharacters.NonWhiteSpace);
            span.setCodeGenerator(new ExpressionCodeGenerator());
        })) {
            do {
                if (AtIdentifier(true)) {
                    AcceptAndMoveNext();
                }
            } while (methodCallOrArrayIndex());

            putCurrentBack();
            Output(SpanKind.Code);
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

                try (AutoCloseable disposable = PushSpanConfig((span, prev) -> {
                    prev.accept(span);
                    span.getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
                })) {
                    right = getLanguage().flipBracket(getCurrentSymbol().getType());
                    BalancingModes modes = BalancingModes.forValue(BalancingModes.BacktrackOnFailure.getValue()
                            | BalancingModes.AllowCommentsAndTemplates.getValue());
                    success = balance(modes);
                    if (!success) {
                        AcceptUntil(JavaSymbolType.LessThan);
                    }
                    if (At(right)) {
                        AcceptAndMoveNext();
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
                    if (At(JavaSymbolType.Identifier) || At(JavaSymbolType.Keyword)) {
                        // accept the dot and return to the start
                        Accept(dot);
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
                    Accept(dot);
                }
            } else if (!At(JavaSymbolType.WhiteSpace) && !At(JavaSymbolType.NewLine)) {
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
            AddMarkerSymbolIfNecessary();
        }

        EnsureCurrent();

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
        Iterable<JavaSymbol> ws = ReadWhile(sym -> sym.getType() == JavaSymbolType.WhiteSpace);
        if (At(JavaSymbolType.NewLine)) {
            Accept(ws);
            AcceptAndMoveNext();
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
        Assert(JavaSymbolType.LeftParenthesis);
        AcceptAndMoveNext();
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        Output(SpanKind.MetaCode);


        try (AutoCloseable disposable = PushSpanConfig((p) -> configureExplicitExpressionSpan(p))) {
            BalancingModes modes = BalancingModes.forValue(BalancingModes.BacktrackOnFailure.getValue()
                    | BalancingModes.NoErrorOnFailure.getValue() | BalancingModes.AllowCommentsAndTemplates.getValue());
            boolean success = balance(modes, JavaSymbolType.LeftParenthesis, JavaSymbolType.RightParenthesis,
                    block.getStart().clone());

            if (!success) {
                AcceptUntil(JavaSymbolType.LessThan);
                getContext().OnError(block.getStart().clone(),
                        RazorResources.getResource(RazorResources.ParseError_Expected_EndOfBlock_Before_EOF),
                        block.getName(), ")", "(");
            }

            // If necessary, put an empty-content marker symbol here
            if (getSpan().getSymbols().size() == 0) {
                try {
                    Accept(new JavaSymbol(getCurrentLocation().clone(), "", JavaSymbolType.Unknown));
                } catch (ArgumentNullException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            // Output the content span and then capture the ")"
            Output(SpanKind.Code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Optional(JavaSymbolType.RightParenthesis);
        if (!getEndOfFile()) {
            putCurrentBack();
        }
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        completeBlock(false);
        Output(SpanKind.MetaCode);
    }

    private void template() {
        if (getContext().IsWithin(BlockType.Template)) {
            getContext().OnError(getCurrentLocation().clone(),
                    RazorResources.getResource(RazorResources.ParseError_InlineMarkup_Blocks_Cannot_Be_Nested));
        }
        Output(SpanKind.Code);

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
        Output(SpanKind.Code);
        boolean wasNested = getIsNested();
        setIsNested(true);


        try (AutoCloseable disposable = PushSpanConfig()) {
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
                && NextIs(JavaSymbolType.LessThan, JavaSymbolType.Colon))
                || getLanguage().isCommentStart(getCurrentSymbol()));
    }

    @Override
    protected void handleEmbeddedTransition() {
        if (getLanguage().isTransition(getCurrentSymbol())) {
            putCurrentBack();
            template();
        } else if (getLanguage().isCommentStart(getCurrentSymbol())) {
            RazorComment();
        }
    }

    private void parseWithOtherParser(Consumer<ParserBase> parseAction) {

        try (AutoCloseable disposable = PushSpanConfig()) {
            getContext().SwitchActiveParser();
            parseAction.accept(getContext().getMarkupParser());
            getContext().SwitchActiveParser();
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
