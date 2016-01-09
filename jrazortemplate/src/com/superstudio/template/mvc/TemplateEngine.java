package com.superstudio.template.mvc;

import com.superstudio.commons.CancellationToken;
import com.superstudio.commons.TextReader;
import com.superstudio.web.razor.GeneratorResults;
import com.superstudio.web.razor.ParserResults;
import com.superstudio.web.razor.RazorEngineHost;
import com.superstudio.web.razor.generator.GeneratedCodeMapping;
import com.superstudio.web.razor.generator.RazorCodeGenerator;
import com.superstudio.web.razor.parser.ParserBase;
import com.superstudio.web.razor.parser.RazorParser;
import com.superstudio.web.razor.text.ITextBuffer;
import com.superstudio.web.razor.text.ITextDocument;
import com.superstudio.web.razor.text.SeekableTextReader;


/**
 * Entry-point to the Razor Template Engine
 *
 */
public class TemplateEngine {
    public static final String DefaultClassName = "Template";
    public static final String DefaultNamespace = "";

    /**
     * Constructs a new RazorTemplateEngine with the specified host
     *
     * @param host
     *            The host which defines the environment in which the generated
     *            template code will live
     * @throws Exception
     */
    public TemplateEngine(RazorEngineHost host) throws Exception {
        if (host == null) {
            //throw new ArgumentNullException("host");
        }

        setHost(host);
    }

    /**
     * The RazorEngineHost which defines the environment in which the generated
     * template code will live
     *
     */
    private RazorEngineHost privateHost;

    public final RazorEngineHost getHost() {
        return privateHost;
    }

    private void setHost(RazorEngineHost value) {
        privateHost = value;
    }

    public final ParserResults ParseTemplate(ITextBuffer input) throws Exception {
        return ParseTemplate(input, null);
    }

    /**
     * Parses the template specified by the TextBuffer and returns it's result
     *
     *
     * IMPORTANT: This does NOT need to be called before GeneratedCode!
     * generateCode will automatically parse the document first.
     *
     * The cancel token provided can be used to cancel the parse. However,
     * please note that the parse occurs _synchronously_, on the callers thread.
     * This parameter is provided so that if the caller is in a background
     * thread with a CancellationToken, it can pass it along to the parser.
     *
     * @param input
     *            The input text to parse
     * @param cancelToken
     *            A token used to cancel the parser
     * @return The resulting parse tree
     * @throws Exception
     */

    public final ParserResults ParseTemplate(ITextBuffer input, CancellationToken cancelToken) throws Exception {
        return ParseTemplateCore(input.toDocument(), cancelToken);
    }


    public final ParserResults ParseTemplate(TextReader input) throws Exception {
        return ParseTemplate(input, null);
    }


    public final ParserResults ParseTemplate(TextReader input, CancellationToken cancelToken) throws Exception {
        return ParseTemplateCore(new SeekableTextReader(input), cancelToken);
    }

    protected ParserResults ParseTemplateCore(ITextDocument input, CancellationToken cancelToken) throws Exception {
        // Construct the parser
        RazorParser parser = CreateParser();
        assert parser != null;
        return parser.Parse(input);
    }

    public final GeneratorResults GenerateCode(ITextBuffer input) throws Exception {
        return GenerateCode(input, null, null, null, null);
    }

    public final GeneratorResults GenerateCode(ITextBuffer input, CancellationToken cancelToken) throws Exception {
        return GenerateCode(input, null, null, null, cancelToken);
    }

    public final GeneratorResults GenerateCode(ITextBuffer input, String className, String rootNamespace,
                                               String sourceFileName) throws Exception {
        return GenerateCode(input, className, rootNamespace, sourceFileName, null);
    }

    /**
     * Parses the template specified by the TextBuffer, generates code for it,
     * and returns the constructed CodeDOM tree
     *
     *
     * The cancel token provided can be used to cancel the parse. However,
     * please note that the parse occurs _synchronously_, on the callers thread.
     * This parameter is provided so that if the caller is in a background
     * thread with a CancellationToken, it can pass it along to the parser.
     *
     * The className, rootNamespace and sourceFileName parameters are optional
     * and override the default specified by the Host. For example, the
     * WebPageRazorHost in System.Web.WebPages.Razor configures the Class Name,
     * Root Namespace and Source File Name based on the virtual path of the page
     * being compiled. However, the built-in RazorEngineHost class uses constant
     * defaults, so the caller will likely want to change them using these
     * parameters
     *
     * @param input
     *            The input text to parse
     * @param cancelToken
     *            A token used to cancel the parser
     * @param className
     *            The name of the generated class, overriding whatever is
     *            specified in the Host. The default value (defined in the Host)
     *            can be used by providing null for this argument
     * @param rootNamespace
     *            The namespace in which the generated class will reside,
     *            overriding whatever is specified in the Host. The default
     *            value (defined in the Host) can be used by providing null for
     *            this argument
     * @param sourceFileName
     *            The file name to use in line pragmas, usually the original
     *            Razor file, overriding whatever is specified in the Host. The
     *            default value (defined in the Host) can be used by providing
     *            null for this argument
     * @return The resulting parse tree AND generated Code DOM tree
     * @throws Exception
     */

    // .NET attributes:
    // [SuppressMessage("Microsoft.Reliability", "CA2000:dispose objects before
    // losing scope", Justification = "Input object would be disposed if we
    // dispose the wrapper. We don't own the input so we don't want to dispose
    // it")]
    public final GeneratorResults GenerateCode(ITextBuffer input, String className, String rootNamespace,
                                               String sourceFileName, CancellationToken cancelToken) throws Exception {
        return GenerateCodeCore(input.toDocument(), className, rootNamespace, sourceFileName, cancelToken);
    }

    // See generateCode override which takes ITextBuffer, and
    // BufferingTextReader for details.
    public final GeneratorResults GenerateCode(TextReader input) throws Exception {

        return GenerateCode(input, null, null, null, null);
    }

    public final GeneratorResults GenerateCode(TextReader input, CancellationToken cancelToken) throws Exception {
        return GenerateCode(input, null, null, null, cancelToken);
    }

    public final GeneratorResults GenerateCode(TextReader input, String className, String rootNamespace,
                                               String sourceFileName) throws Exception {
        return GenerateCode(input, className, rootNamespace, sourceFileName, null);
    }


    public final GeneratorResults GenerateCode(TextReader input, String className, String rootNamespace,
                                               String sourceFileName, CancellationToken cancelToken) throws Exception {
        return GenerateCodeCore(new SeekableTextReader(input), className, rootNamespace, sourceFileName, cancelToken);
    }

    protected GeneratorResults GenerateCodeCore(ITextDocument input, String className, String rootNamespace,
                                                String sourceFileName, CancellationToken cancelToken) throws Exception {
        className = ((((className != null) ? className : getHost().getDefaultClassName())) != null)
                ? ((className != null) ? className : getHost().getDefaultClassName()) : DefaultClassName;
        rootNamespace = ((((rootNamespace != null) ? rootNamespace : getHost().getDefaultNamespace())) != null)
                ? ((rootNamespace != null) ? rootNamespace : getHost().getDefaultNamespace()) : DefaultNamespace;

        // Run the parser
        RazorParser parser = CreateParser();
        assert parser != null;
        ParserResults results = parser.Parse(input);

        // Generate code
        RazorCodeGenerator generator = CreateCodeGenerator(className, rootNamespace, sourceFileName);
        generator.setDesignTimeMode(getHost().getDesignTimeMode());
        generator.visit(results);

        // Post process code
        getHost().postProcessGeneratedCode(generator.getContext());

        // Extract design-time mappings
        java.util.Map<Integer, GeneratedCodeMapping> designTimeLineMappings = null;
        if (getHost().getDesignTimeMode()) {
            designTimeLineMappings = generator.getContext().getCodeMappings();
        }

        // Collect results and return
        return new GeneratorResults(results, generator.getContext().getCompileUnit(), designTimeLineMappings);
    }

    protected RazorCodeGenerator CreateCodeGenerator(String className, String rootNamespace, String sourceFileName)
            throws Exception {
        return getHost().decorateCodeGenerator(
                getHost().getCodeLanguage().createCodeGenerator(className, rootNamespace, sourceFileName, getHost()));
    }

    protected RazorParser CreateParser() throws Exception {
        ParserBase codeParser = getHost().getCodeLanguage().createCodeParser();
        ParserBase markupParser = getHost().createMarkupParser();

        RazorParser tempVar = new RazorParser(getHost().decorateCodeParser(codeParser),
                getHost().decorateMarkupParser(markupParser));
        tempVar.setDesignTimeMode(getHost().getDesignTimeMode());
        return tempVar;
    }


}