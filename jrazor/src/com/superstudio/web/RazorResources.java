package com.superstudio.web;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * A strongly-typed resource class, for looking up localized strings, etc.
 */
public class RazorResources {
    /**
     * Looks up a localized string similar to The active parser must the same as either the markup or code parser..
     */
    public static final String ActiveParser_Must_Be_Code_Or_Markup_Parser = "ActiveParser_Must_Be_Code_Or_Markup_Parser";
    public static final String Argument_Cannot_Be_Null_Or_Empty = "Argument_Cannot_Be_Null_Or_Empty";
    /**
     * Looks up a localized string similar to Block cannot be built because a Type has not been specified in the BlockBuilder.
     */
    public static final String Block_Type_Not_Specified = "Block_Type_Not_Specified";
    /**
     * Looks up a localized string similar to code.
     */
    public static final String BlockName_Code = "BlockName_Code";

    /**
     * Looks up a localized string similar to explicit expression.
     */
    public static final String BlockName_ExplicitExpression = "BlockName_ExplicitExpression";

    private ResourceBundle resourceBundle;
    private Locale locale;

    public RazorResources(Locale locale) {
        this.locale = locale;
        this.resourceBundle = ResourceBundle.getBundle("com.superstudio.web.razor.RazorResources", locale);
    }


    public String getResource(String key) {

        return this.resourceBundle.getString(key);
    }


    public static final String getResources(String key, Locale locale) {
        return new RazorResources(locale).getResource(key);
    }


    /**
     * Looks up a localized string similar to The &quot;CancelBacktrack&quot; method can be called only while in a look-ahead process started with the &quot;BeginLookahead&quot; method..
     */
    public static final String CancelBacktrack_Must_Be_Called_Within_Lookahead = "CancelBacktrack_Must_Be_Called_Within_Lookahead";


    /**
     * Looks up a localized string similar to Cannot call CreateCodeWriter, a CodeWriter was not provided to the create method.
     */
    public static final String CreateCodeWriter_NoCodeWriter = "CreateCodeWriter_NoCodeWriter";

    /**
     * Looks up a localized string similar to &lt;&lt;character literal&gt;&gt;.
     */
    public static final String JavaSymbol_CharacterLiteral = "JavaSymbol_CharacterLiteral";

    /**
     * Looks up a localized string similar to &lt;&lt;comment&gt;&gt;.
     */
    public static final String JavaSymbol_Comment = "JavaSymbol_Comment";

    /**
     * Looks up a localized string similar to &lt;&lt;identifier&gt;&gt;.
     */
    public static final String JavaSymbol_Identifier = "JavaSymbol_Identifier";

    /**
     * Looks up a localized string similar to &lt;&lt;integer literal&gt;&gt;.
     */
    public static final String JavaSymbol_IntegerLiteral = "";

    /**
     * Looks up a localized string similar to &lt;&lt;keyword&gt;&gt;.
     */
    public static final String JavaSymbol_Keyword = "JavaSymbol_Keyword";

    /**
     * Looks up a localized string similar to &lt;&lt;newline sequence&gt;&gt;.
     */
    public static final String JavaSymbol_Newline = "JavaSymbol_Newline";

    /**
     * Looks up a localized string similar to &lt;&lt;real literal&gt;&gt;.
     */
    public static final String JavaSymbol_RealLiteral = "JavaSymbol_RealLiteral";

    /**
     * Looks up a localized string similar to &lt;&lt;string literal&gt;&gt;.
     */
    public static final String JavaSymbol_StringLiteral = "";

    /**
     * Looks up a localized string similar to &lt;&lt;white space&gt;&gt;.
     */
    public static final String JavaSymbol_Whitespace = "JavaSymbol_Whitespace";

    /**
     * Looks up a localized string similar to &quot;EndBlock&quot; was called without a matching call to &quot;StartBlock&quot;..
     */
    public static final String EndBlock_Called_Without_Matching_StartBlock = "EndBlock_Called_Without_Matching_StartBlock";

    /**
     * Looks up a localized string similar to &quot;{0}&quot; character.
     */
    public static final String ErrorComponent_Character = "ErrorComponent_Character";

    /**
     * Looks up a localized string similar to end of file.
     */
    public static final String ErrorComponent_EndOfFile = "ErrorComponent_EndOfFile";

    /**
     * Looks up a localized string similar to line break.
     */
    public static final String ErrorComponent_Newline = "ErrorComponent_Newline";

    /**
     * Looks up a localized string similar to space or line break.
     */
    public static final String ErrorComponent_Whitespace = "ErrorComponent_Whitespace";

    /**
     * Looks up a localized string similar to &lt;&lt;newline sequence&gt;&gt;.
     */
    public static final String HtmlSymbol_NewLine = "HtmlSymbol_NewLine";

    /**
     * Looks up a localized string similar to &lt;&lt;razor comment&gt;&gt;.
     */
    public static final String HtmlSymbol_RazorComment = "HtmlSymbol_RazorComment";

    /**
     * Looks up a localized string similar to &lt;&lt;text&gt;&gt;.
     */
    public static final String HtmlSymbol_Text = "HtmlSymbol_Text";

    /**
     * Looks up a localized string similar to &lt;&lt;white space&gt;&gt;.
     */
    public static final String HtmlSymbol_WhiteSpace = "HtmlSymbol_WhiteSpace";

    /**
     * Looks up a localized string similar to Cannot use built-in razorComment handler, language characteristics does not define the CommentStart, CommentStar and CommentBody known symbol types or parser does not override TokenizerBackedParser.OutputSpanBeforeRazorComment.
     */
    public static final String Language_Does_Not_Support_RazorComment = "Language_Does_Not_Support_RazorComment";


    /**
     * Looks up a localized string similar to The &quot;@&quot; character must be followed by a &quot;:&quot;, &quot;(&quot;, or a C# identifier.  If you intended to switch to markup, use an HTML start tag, for example:
     *
     * @if(isLoggedIn) {
     * &lt;p&gt;Hello, @user!&lt;/p&gt;
     * }.
     */
    public static final String ParseError_AtInCode_Must_Be_Followed_By_Colon_Paren_Or_Identifier_Start = "ParseError_AtInCode_Must_Be_Followed_By_Colon_Paren_Or_Identifier_Start";


    /**
     * Looks up a localized string similar to End of file was reached before the end of the block comment.  All comments started with &quot;/*&quot; sequence must be terminated with a matching &quot;
     */
    public static final String ParseError_BlockComment_Not_Terminated = "ParseError_BlockComment_Not_Terminated";


    /**
     * Looks up a localized string similar to The &quot;{0}&quot; block was not terminated.  All &quot;{0}&quot; statements must be terminated with a matching &quot;{1}&quot;..
     */
    public static final String ParseError_BlockNotTerminated = "ParseError_BlockNotTerminated";


    /**
     * Looks up a localized string similar to An opening &quot;{0}&quot; is missing the corresponding closing &quot;{1}&quot;..
     */
    public static final String ParseError_Expected_CloseBracket_Before_EOF = "ParseError_Expected_CloseBracket_Before_EOF";


    /**
     * Looks up a localized string similar to The {0} block is missing a closing &quot;{1}&quot; character.  Make sure you have a matching &quot;{1}&quot; character for all the &quot;{2}&quot; characters within this block, and that none of the &quot;{1}&quot; characters are being interpreted as markup..
     */
    public static final String ParseError_Expected_EndOfBlock_Before_EOF = "ParseError_Expected_EndOfBlock_Before_EOF";


    /**
     * Looks up a localized string similar to Expected &quot;{0}&quot;..
     */
    public static final String ParseError_Expected_X = "ParseError_Expected_X";


    /**
     * Looks up a localized string similar to Helper blocks cannot be nested within each other..
     */
    public static final String ParseError_Helpers_Cannot_Be_Nested = "ParseError_Helpers_Cannot_Be_Nested";


    /**
     * Looks up a localized string similar to The &apos;inherits&apos; keyword must be followed by a type name on the same line..
     */
    public static final String ParseError_InheritsKeyword_Must_Be_Followed_By_TypeName = "ParseError_InheritsKeyword_Must_Be_Followed_By_TypeName";


    /**
     * Looks up a localized string similar to Inline markup blocks (@&lt;p&gt;Content&lt;/p&gt;) cannot be nested.  Only one level of inline markup is allowed..
     */
    public static final String ParseError_InlineMarkup_Blocks_Cannot_Be_Nested = "ParseError_InlineMarkup_Blocks_Cannot_Be_Nested";


    /**
     * Looks up a localized string similar to &quot;{1}&quot; is not a valid value for the &quot;{0}&quot; option. The &quot;Option {0}&quot; statement must be followed by either &quot;On&quot; or &quot;Off&quot;. .
     */
    public static final String ParseError_InvalidOptionValue = "ParseError_InvalidOptionValue";


    /**
     * Looks up a localized string similar to Markup in a code block must start with a tag and all start tags must be matched with end tags.  Do not use unclosed tags like &quot;&lt;br&gt;&quot;.  Instead use self-closing tags like &quot;&lt;br/&gt;&quot;..
     */
    public static final String ParseError_MarkupBlock_Must_Start_With_Tag = "ParseError_MarkupBlock_Must_Start_With_Tag";


    /**
     * Looks up a localized string similar to Expected a &quot;{0}&quot; after the helper name..
     */
    public static final String ParseError_MissingCharAfterHelperName = "ParseError_MissingCharAfterHelperName";


    /**
     * Looks up a localized string similar to Expected a &quot;{0}&quot; after the helper parameters..
     */
    public static final String ParseError_MissingCharAfterHelperParameters = "ParseError_MissingCharAfterHelperParameters";


    /**
     * Looks up a localized string similar to The &quot;{0}&quot; element was not closed.  All elements must be either self-closing or have a matching end tag..
     */
    public static final String ParseError_MissingEndTag = "ParseError_MissingEndTag";


    /**
     * Looks up a localized string similar to Sections cannot be empty.  The &quot;@section&quot; keyword must be followed by a block of markup surrounded by &quot;{}&quot;.  For example:
     *
     * @section Sidebar {
     * &lt;!-- Markup and text goes here --&gt;
     * .
     */
    public static final String ParseError_MissingOpenBraceAfterSection = "ParseError_MissingOpenBraceAfterSection";


    /**
     * Looks up a localized string similar to Namespace imports and type aliases cannot be placed within code blocks.  They must immediately follow an &quot;@&quot; character in markup.  It is recommended that you put them at the top of the page, as in the following example:
     *
     * @using System.Drawing;
     * @{ // OK here to use types from System.Drawing in the page.
     * .
     */
    public static final String ParseError_NamespaceImportAndTypeAlias_Cannot_Exist_Within_CodeBlock = "ParseError_NamespaceImportAndTypeAlias_Cannot_Exist_Within_CodeBlock";


    /**
     * Looks up a localized string similar to The &quot;Imports&quot; keyword must be followed by a namespace or a type alias on the same line..
     */
    public static final String ParseError_NamespaceOrTypeAliasExpected = "ParseError_NamespaceOrTypeAliasExpected";


    /**
     * Looks up a localized string similar to Outer tag is missing a name. The first character of a markup block must be an HTML tag with a valid name..
     */
    public static final String ParseError_OuterTagMissingName = "ParseError_OuterTagMissingName";


    /**
     * Looks up a localized string similar to End of file was reached before the end of the block comment.  All comments that start with the &quot;@*&quot; sequence must be terminated with a matching &quot;*@&quot; sequence..
     */
    public static final String ParseError_RazorComment_Not_Terminated = "ParseError_RazorComment_Not_Terminated";


    /**
     * Looks up a localized string similar to &quot;{0}&quot; is a reserved word and cannot be used in implicit expressions.  An explicit expression (&quot;@()&quot;) must be used..
     */
    public static final String ParseError_ReservedWord = "ParseError_ReservedWord";


    /**
     * Looks up a localized string similar to Section blocks (&quot;{0}&quot;) cannot be nested.  Only one level of section blocks are allowed..
     */
    public static final String ParseError_Sections_Cannot_Be_Nested = "ParseError_Sections_Cannot_Be_Nested";


    /**
     * Looks up a localized string similar to Expected a &quot;{0}&quot; but found a &quot;{1}&quot;.  Block statements must be enclosed in &quot;{{&quot; and &quot;}}&quot;.  You cannot use single-statement control-flow statements in CSHTML pages. For example, the following is not allowed:
     *
     * @if(isLoggedIn) &lt;p&gt;Hello, @user&lt;/p&gt;
     * <p>
     * Instead, wrap the contents of the block in &quot;{{}}&quot;:
     * @if(isLoggedIn) {{
     * &lt;p&gt;Hello, @user&lt;/p&gt;
     * }.
     */
    public static final String ParseError_SingleLine_ControlFlowStatements_Not_Allowed = "ParseError_SingleLine_ControlFlowStatements_Not_Allowed";


    /**
     * Looks up a localized string similar to &quot;&lt;text&gt;&quot; and &quot;&lt;/text&gt;&quot; tags cannot contain attributes..
     */
    public static final String ParseError_TextTagCannotContainAttributes = "ParseError_TextTagCannotContainAttributes";


    /**
     * Looks up a localized string similar to Unexpected &quot;{0}&quot;.
     */
    public static final String ParseError_Unexpected = "ParseError_Unexpected";


    /**
     * Looks up a localized string similar to Unexpected {0} after helper keyword.  All helpers must have a name which starts with an &quot;_&quot; or alphabetic character. The remaining characters must be either &quot;_&quot; or alphanumeric..
     */
    public static final String ParseError_Unexpected_Character_At_Helper_Name_Start = "ParseError_Unexpected_Character_At_Helper_Name_Start";


    /**
     * Looks up a localized string similar to Unexpected {0} after section keyword.  Section names must start with an &quot;_&quot; or alphabetic character, and the remaining characters must be either &quot;_&quot; or alphanumeric..
     */
    public static final String ParseError_Unexpected_Character_At_Section_Name_Start = "ParseError_Unexpected_Character_At_Section_Name_Start";


    /**
     * Looks up a localized string similar to &quot;{0}&quot; is not valid at the start of a code block.  Only identifiers, keywords, comments, &quot;(&quot; and &quot;{{&quot; are valid..
     */
    public static final String ParseError_Unexpected_Character_At_Start_Of_CodeBlock_CS = "ParseError_Unexpected_Character_At_Start_Of_CodeBlock_CS";


    /**
     * Looks up a localized string similar to &quot;{0}&quot; is not valid at the start of a code block.  Only identifiers, keywords, comments, and &quot;(&quot; are valid..
     */
    public static final String ParseError_Unexpected_Character_At_Start_Of_CodeBlock_VB = "ParseError_Unexpected_Character_At_Start_Of_CodeBlock_VB";


    /**
     * Looks up a localized string similar to End-of-file was found after the &quot;@&quot; character.  &quot;@&quot; must be followed by a valid code block.  If you want to output an &quot;@&quot;, escape it using the sequence: &quot;@@&quot;.
     */
    public static final String ParseError_Unexpected_EndOfFile_At_Start_Of_CodeBlock = "ParseError_Unexpected_EndOfFile_At_Start_Of_CodeBlock";


    /**
     * Looks up a localized string similar to Unexpected &quot;{0}&quot; keyword after &quot;@&quot; character.  Once inside code, you do not need to prefix constructs like &quot;{0}&quot; with &quot;@&quot;..
     */
    public static final String ParseError_Unexpected_Keyword_After_At = "ParseError_Unexpected_Keyword_After_At";


    /**
     * Looks up a localized string similar to Unexpected &quot;{&quot; after &quot;@&quot; character. Once inside the body of a code block (@if {}, @{}, etc.) you do not need to use &quot;@{&quot; to switch to code..
     */
    public static final String ParseError_Unexpected_Nested_CodeBlock = "ParseError_Unexpected_Nested_CodeBlock";


    /**
     * Looks up a localized string similar to A space or line break was encountered after the &quot;@&quot; character.  Only valid identifiers, keywords, comments, &quot;(&quot; and &quot;{&quot; are valid at the start of a code block and they must occur immediately following &quot;@&quot; with no space in between..
     */
    public static final String ParseError_Unexpected_WhiteSpace_At_Start_Of_CodeBlock_CS = "ParseError_Unexpected_WhiteSpace_At_Start_Of_CodeBlock_CS";


    /**
     * Looks up a localized string similar to A space or line break was encountered after the &quot;@&quot; character.  Only valid identifiers, keywords, comments, and &quot;(&quot; are valid at the start of a code block and they must occur immediately following &quot;@&quot; with no space in between..
     */
    public static final String ParseError_Unexpected_WhiteSpace_At_Start_Of_CodeBlock_VB = "ParseError_Unexpected_WhiteSpace_At_Start_Of_CodeBlock_VB";


    /**
     * Looks up a localized string similar to Encountered end tag &quot;{0}&quot; with no matching start tag.  Are your start/end tags properly balanced?.
     */
    public static final String ParseError_UnexpectedEndTag = "ParseError_UnexpectedEndTag";


    /**
     * Looks up a localized string similar to End of file or an unexpected character was reached before the &quot;{0}&quot; tag could be parsed.  Elements inside markup blocks must be complete. They must either be self-closing (&quot;&lt;br /&gt;&quot;) or have matching end tags (&quot;&lt;p&gt;Hello&lt;/p&gt;&quot;).  If you intended to display a &quot;&lt;&quot; character, use the &quot;&amp;lt;&quot; HTML entity..
     */
    public static final String ParseError_UnfinishedTag = "ParseError_UnfinishedTag";


    /**
     * Looks up a localized string similar to Unknown option: &quot;{0}&quot;..
     */
    public static final String ParseError_UnknownOption = "ParseError_UnknownOption";


    /**
     * Looks up a localized string similar to Unterminated string literal. Strings that start with a quotation mark (&quot;) must be terminated before the end of the line.  However, strings that start with @ and a quotation mark (@&quot;) can span multiple lines. Closing HTML tags within a string literal may trigger this error message..
     */
    public static final String ParseError_Unterminated_String_Literal = "ParseError_Unterminated_String_Literal";


    /**
     * Looks up a localized string similar to Helper parameter list is missing a closing &quot;)&quot;..
     */
    public static final String ParseError_UnterminatedHelperParameterList = "ParseError_UnterminatedHelperParameterList";


    /**
     * Looks up a localized string similar to Parser was started with a null Context property.  The Context property must be set BEFORE calling any methods on the parser..
     */
    public static final String Parser_Context_Not_Set = "Parser_Context_Not_Set";


    /**
     * Looks up a localized string similar to Cannot complete the tree, StartBlock must be called at least once..
     */
    public static final String ParserContext_CannotCompleteTree_NoRootBlock = "ParserContext_CannotCompleteTree_NoRootBlock";


    /**
     * Looks up a localized string similar to Cannot complete the tree, there are still open blocks..
     */
    public static final String ParserContext_CannotCompleteTree_OutstandingBlocks = "ParserContext_CannotCompleteTree_OutstandingBlocks";


    /**
     * Looks up a localized string similar to Cannot finish span, there is no current block. Call StartBlock at least once before finishing a span.
     */
    public static final String ParserContext_NoCurrentBlock = "ParserContext_NoCurrentBlock";


    /**
     * Looks up a localized string similar to Cannot complete action, the parser has finished. Only CompleteParse can be called to extract the final parser results after the parser has finished.
     */
    public static final String ParserContext_ParseComplete = "ParserContext_ParseComplete";


    /**
     * Looks up a localized string similar to Missing value for session state directive..
     */
    public static final String ParserEror_SessionDirectiveMissingValue = "ParserEror_SessionDirectiveMissingValue";


    /**
     * Looks up a localized string similar to The parser provided to the ParserContext was not a Markup Parser..
     */
    public static final String ParserIsNotAMarkupParser = "ParserIsNotAMarkupParser";


    /**
     * Looks up a localized string similar to @section Header { ....
     */
    public static final String SectionExample_CS = "SectionExample_CS";


    /**
     * Looks up a localized string similar to The {0} property of the {1} structure cannot be null..
     */
    public static final String Structure_Member_CannotBeNull = "Structure_Member_CannotBeNull";


    /**
     * Looks up a localized string similar to &lt;&lt;unknown&gt;&gt;.
     */
    public static final String Symbol_Unknown = "Symbol_Unknown";


    /**
     * Looks up a localized string similar to Cannot resume this symbol. Only the symbol immediately preceding the current one can be resumed..
     */
    public static final String Tokenizer_CannotResumeSymbolUnlessIsPrevious = "Tokenizer_CannotResumeSymbolUnlessIsPrevious";


    /**
     * Looks up a localized string similar to In order to put a symbol back, it must have been the symbol which ended at the current position. The specified symbol ends at {0}, but the current position is {1}.
     */
    public static final String TokenizerView_CannotPutBack = "TokenizerView_CannotPutBack";


    /**
     * Looks up a localized string similar to [BG][{0}] Shutdown.
     */
    public static final String Trace_BackgroundThreadShutdown = "Trace_BackgroundThreadShutdown";


    /**
     * Looks up a localized string similar to [BG][{0}] Startup.
     */
    public static final String Trace_BackgroundThreadStart = "Trace_BackgroundThreadStart";


    /**
     * Looks up a localized string similar to [BG][{0}] {1} changes arrived.
     */
    public static final String Trace_ChangesArrived = "Trace_ChangesArrived";


    /**
     * Looks up a localized string similar to [BG][{0}] Discarded {1} changes.
     */
    public static final String Trace_ChangesDiscarded = "Trace_ChangesDiscarded";


    /**
     * Looks up a localized string similar to [BG][{0}] Collecting {1} discarded changes.
     */
    public static final String Trace_CollectedDiscardedChanges = "Trace_CollectedDiscardedChanges";


    /**
     * Looks up a localized string similar to Disabled.
     */
    public static final String Trace_Disabled = "Trace_Disabled";


    /**
     * Looks up a localized string similar to [P][{0}] {3} Change in {2} milliseconds: {1}.
     */
    public static final String Trace_EditorProcessedChange = "Trace_EditorProcessedChange";


    /**
     * Looks up a localized string similar to [P][{0}] Received Change: {1}.
     */
    public static final String Trace_EditorReceivedChange = "Trace_EditorReceivedChange";


    /**
     * Looks up a localized string similar to Enabled.
     */
    public static final String Trace_Enabled = "Trace_Enabled";


    /**
     * Looks up a localized string similar to [Razor] {0}.
     */
    public static final String Trace_Format = "Trace_Format";


    /**
     * Looks up a localized string similar to [BG][{0}] no changes arrived?.
     */
    public static final String Trace_NoChangesArrived = "Trace_NoChangesArrived";


    /**
     * Looks up a localized string similar to [BG][{0}] parse Complete in {1} milliseconds.
     */
    public static final String Trace_ParseComplete = "Trace_ParseComplete";


    /**
     * Looks up a localized string similar to [M][{0}] Queuing parse for: {1}.
     */
    public static final String Trace_QueuingParse = "Trace_QueuingParse";


    /**
     * Looks up a localized string similar to [Razor] Editor Tracing {0}.
     */
    public static final String Trace_Startup = "Trace_Startup";


    /**
     * Looks up a localized string similar to [BG][{0}] Trees Compared in {1} milliseconds. Different = {2}.
     */
    public static final String Trace_TreesCompared = "Trace_TreesCompared";
}
