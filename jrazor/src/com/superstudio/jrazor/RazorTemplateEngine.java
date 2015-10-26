package com.superstudio.jrazor;

import com.superstudio.commons.CancellationToken;
import com.superstudio.commons.TextReader;
import com.superstudio.jrazor.generator.GeneratedCodeMapping;
import com.superstudio.jrazor.generator.RazorCodeGenerator;
import com.superstudio.jrazor.parser.ParserBase;
import com.superstudio.jrazor.parser.RazorParser;
import com.superstudio.jrazor.text.ITextBuffer;
import com.superstudio.jrazor.text.ITextDocument;
import com.superstudio.jrazor.text.SeekableTextReader;



/**
 * Entry-point to the Razor Template Engine
 * 
 */
public class RazorTemplateEngine {
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
	public RazorTemplateEngine(RazorEngineHost host) throws Exception {
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

	public final ParserResults parseTemplate(ITextBuffer input) throws Exception {
		return parseTemplate(input, null);
	}

	/**
	 * Parses the template specified by the TextBuffer and returns it's result
	 * 
	 * 
	 * IMPORTANT: This does NOT need to be called before GeneratedCode!
	 * GenerateCode will automatically parse the document first.
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
	 
	 
	// [SuppressMessage("Microsoft.Reliability", "CA2000:Dispose objects before
	// losing scope", Justification = "Input object would be disposed if we
	// dispose the wrapper. We don't own the input so we don't want to dispose
	// it")]
	public final ParserResults parseTemplate(ITextBuffer input, CancellationToken cancelToken) throws Exception {
		return parseTemplateCore(input.toDocument(), cancelToken);
	}

	// See ParseTemplate(ITextBuffer, CancellationToken?),
	// this overload simply wraps a TextReader in a TextBuffer (see ITextBuffer
	// and BufferingTextReader)
	public final ParserResults parseTemplate(TextReader input) throws Exception {
		return parseTemplate(input, null);
	}

	 
	 
	// [SuppressMessage("Microsoft.Reliability", "CA2000:Dispose objects before
	// losing scope", Justification = "Input object would be disposed if we
	// dispose the wrapper. We don't own the input so we don't want to dispose
	// it")]
	public final ParserResults parseTemplate(TextReader input, CancellationToken cancelToken) throws Exception {
		return parseTemplateCore(new SeekableTextReader(input), cancelToken);
	}

	protected ParserResults parseTemplateCore(ITextDocument input, CancellationToken cancelToken) throws Exception {
		// Construct the parser
		RazorParser parser = createParser();
		assert parser != null;
		return parser.parse(input);
	}

	public final GeneratorResults generateCode(ITextBuffer input) throws Exception {
		return generateCode(input, null, null, null, null);
	}

	public final GeneratorResults generateCode(ITextBuffer input, CancellationToken cancelToken) throws Exception {
		return generateCode(input, null, null, null, cancelToken);
	}

	public final GeneratorResults generateCode(ITextBuffer input, String className, String rootNamespace,
			String sourceFileName) throws Exception {
		return generateCode(input, className, rootNamespace, sourceFileName, null);
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
	 
	 
	// [SuppressMessage("Microsoft.Reliability", "CA2000:Dispose objects before
	// losing scope", Justification = "Input object would be disposed if we
	// dispose the wrapper. We don't own the input so we don't want to dispose
	// it")]
	public final GeneratorResults generateCode(ITextBuffer input, String className, String rootNamespace,
			String sourceFileName, CancellationToken cancelToken) throws Exception {
		return generateCodeCore(input.toDocument(), className, rootNamespace, sourceFileName, cancelToken);
	}

	// See GenerateCode override which takes ITextBuffer, and
	// BufferingTextReader for details.
	public final GeneratorResults generateCode(TextReader input) throws Exception {
		
		return generateCode(input, null, null, null, null);
	}

	public final GeneratorResults generateCode(TextReader input, CancellationToken cancelToken) throws Exception {
		return generateCode(input, null, null, null, cancelToken);
	}

	public final GeneratorResults generateCode(TextReader input, String className, String rootNamespace,
			String sourceFileName) throws Exception {
		return generateCode(input, className, rootNamespace, sourceFileName, null);
	}

	
	public final GeneratorResults generateCode(TextReader input, String className, String rootNamespace,
			String sourceFileName, CancellationToken cancelToken) throws Exception {
		return generateCodeCore(new SeekableTextReader(input), className, rootNamespace, sourceFileName, cancelToken);
	}

	protected GeneratorResults generateCodeCore(ITextDocument input, String className, String rootNamespace,
			String sourceFileName, CancellationToken cancelToken) throws Exception {
		className = ((((className != null) ? className : getHost().getDefaultClassName())) != null)
				? ((className != null) ? className : getHost().getDefaultClassName()) : DefaultClassName;
		rootNamespace = ((((rootNamespace != null) ? rootNamespace : getHost().getDefaultNamespace())) != null)
				? ((rootNamespace != null) ? rootNamespace : getHost().getDefaultNamespace()) : DefaultNamespace;

		// Run the parser
		RazorParser parser = createParser();
		assert parser != null;
		ParserResults results = parser.parse(input);

		// Generate code
		RazorCodeGenerator generator = createCodeGenerator(className, rootNamespace, sourceFileName);
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

	public RazorCodeGenerator createCodeGenerator(String className, String rootNamespace, String sourceFileName)
			throws Exception {
		return getHost().decorateCodeGenerator(
				getHost().getCodeLanguage().createCodeGenerator(className, rootNamespace, sourceFileName, getHost()));
	}

	public RazorParser createParser() throws Exception {
		ParserBase codeParser = getHost().getCodeLanguage().createCodeParser();
		ParserBase markupParser = getHost().createMarkupParser();

		RazorParser tempVar = new RazorParser(getHost().decorateCodeParser(codeParser),
				getHost().decorateMarkupParser(markupParser));
		tempVar.setDesignTimeMode(getHost().getDesignTimeMode());
		return tempVar;
	}
	
	
}