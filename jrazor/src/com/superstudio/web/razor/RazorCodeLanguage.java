package com.superstudio.web.razor;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.language.java.JavaRazorCodeLanguage;
import com.superstudio.web.razor.generator.RazorCodeGenerator;
import com.superstudio.web.razor.parser.ParserBase;
import org.apache.commons.lang3.StringUtils;


/**
 * Represents a code language in Razor.
 * 
 */
public abstract class RazorCodeLanguage {
	private static java.util.Map<String, RazorCodeLanguage> _services = new java.util.HashMap<String, RazorCodeLanguage>() {
		{

			put("jhtml",new JavaRazorCodeLanguage());

		}
	};

	/**
	 * Gets the list of registered languages mapped to file extensions (without
	 * a ".")
	 * 
	 */
	public static java.util.Map<String, RazorCodeLanguage> getLanguages() {
		return _services;
	}

	/**
	 * The name of the language (for use in
	 * System.Web.Compilation.BuildProvider.getDefaultCompilerTypeForLanguage)
	 * 
	 */
	public abstract String getLanguageName();

	/**
	 * The type of the CodeDOM provider for this language
	 * 
	 */
	public abstract java.lang.Class getCodeDomProviderType();

	/**
	 * Gets the RazorCodeLanguage registered for the specified file extension
	 * 
	 * @param fileExtension
	 *            The extension, with or without a "."
	 * @return The language registered for that extension
	 */
	public static RazorCodeLanguage getLanguageByExtension(String fileExtension) {
		//StringUtils.removeStart()
		RazorCodeLanguage service = getLanguages().get(StringHelper.trimStart(fileExtension, '.'));
		return service;
	}

	/**
	 * Constructs the code parser. Must return a new instance on EVERY call to
	 * ensure thread-safety
	 * 
	 */
	public abstract ParserBase createCodeParser();

	/**
	 * Constructs the code generator. Must return a new instance on EVERY call
	 * to ensure thread-safety
	 * @throws Exception 
	 * 
	 */
	public abstract RazorCodeGenerator createCodeGenerator(String className, String rootNamespaceName,
														   String sourceFileName, RazorEngineHost host) throws Exception;
}