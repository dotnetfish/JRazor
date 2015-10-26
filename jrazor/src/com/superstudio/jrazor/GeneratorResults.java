package com.superstudio.jrazor;

import com.superstudio.codedom.CodeCompileUnit;
import com.superstudio.jrazor.generator.GeneratedCodeMapping;
import com.superstudio.jrazor.parser.syntaxTree.*;




/** 
 Represents results from code generation (and parsing, since that is a pre-requisite of code generation)
 
 
 Since this inherits from ParserResults, it has all the data from ParserResults, and simply adds code generation data
 
*/
public class GeneratorResults extends ParserResults
{
	public GeneratorResults(ParserResults parserResults, CodeCompileUnit generatedCode, java.util.Map<Integer, GeneratedCodeMapping> designTimeLineMappings)
	{
		this(parserResults.getDocument(), parserResults.getParserErrors(), generatedCode, designTimeLineMappings);
	}

	public GeneratorResults(Block document, java.util.List<RazorError> parserErrors, CodeCompileUnit generatedCode, java.util.Map<Integer, GeneratedCodeMapping> designTimeLineMappings)
	{
		this(parserErrors.isEmpty(), document, parserErrors, generatedCode, designTimeLineMappings);
	}

	protected GeneratorResults(boolean success, Block document, java.util.List<RazorError> parserErrors, CodeCompileUnit generatedCode, java.util.Map<Integer, GeneratedCodeMapping> designTimeLineMappings)
	{
		super(success, document, parserErrors);
		setGeneratedCode(generatedCode);
		setDesignTimeLineMappings(designTimeLineMappings);
	}

	/** 
	 The generated code
	 
	*/
	private CodeCompileUnit privateGeneratedCode;
	public final CodeCompileUnit getGeneratedCode()
	{
		return privateGeneratedCode;
	}
	private void setGeneratedCode(CodeCompileUnit value)
	{
		privateGeneratedCode = value;
	}

	/** 
	 If design-time mode was used in the Code Generator, this will contain the dictionary
	 of design-time generated code mappings
	 
	*/
	private java.util.Map<Integer, GeneratedCodeMapping> privateDesignTimeLineMappings;
	public final java.util.Map<Integer, GeneratedCodeMapping> getDesignTimeLineMappings()
	{
		return privateDesignTimeLineMappings;
	}
	private void setDesignTimeLineMappings(java.util.Map<Integer, GeneratedCodeMapping> value)
	{
		privateDesignTimeLineMappings = value;
	}
}