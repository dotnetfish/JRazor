package com.superstudio.language.java;

import com.superstudio.jrazor.RazorCodeLanguage;
import com.superstudio.jrazor.RazorEngineHost;
import com.superstudio.jrazor.generator.RazorCodeGenerator;
import com.superstudio.jrazor.parser.*;
import com.superstudio.language.java.parser.JavaCodeParser;





/** 
 Defines the C# Code Language for Razor
 
*/
public class JavaRazorCodeLanguage extends RazorCodeLanguage
{
	private static final String LanguageName = "java";

	/** 
	 Returns the name of the language: "csharp"
	 
	*/
	@Override
	public String getLanguageName()
	{
		return LanguageName;
	}

	/** 
	 Returns the type of the CodeDOM provider for this language
	 
	*/
	@Override
	public java.lang.Class<?> getCodeDomProviderType()
	{
		//return CSharpCodeProvider.class;
		return JavaCodeProvider.class;
	}

	/** 
	 Constructs a new instance of the code parser for this language
	 
	*/
	@Override
	public ParserBase createCodeParser()
	{
		return new JavaCodeParser();
	}

	/** 
	 Constructs a new instance of the code generator for this language with the specified settings
	 * @throws Exception 
	 
	*/
	@Override
	public RazorCodeGenerator createCodeGenerator(String className, String rootNamespaceName, String sourceFileName, RazorEngineHost host) throws Exception
	{
		return new JavaRazorCodeGenerator(className, rootNamespaceName, sourceFileName, host);
	}

}