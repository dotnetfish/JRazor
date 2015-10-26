package com.superstudio.codedom.compiler;

import com.superstudio.codedom.*;
import com.superstudio.commons.exception.ArgumentException;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.commons.io.TextWriter;

public interface ICodeGenerator
{
	boolean isValidIdentifier(String value);

	void validateIdentifier(String value) throws ArgumentException;

	String createEscapedIdentifier(String value);

	String createValidIdentifier(String value);

	String getTypeOutput(CodeTypeReference type);

	boolean supports(GeneratorSupport supports);

	void generateCodeFromExpression(CodeExpression e, TextWriter w, CodeGeneratorOptions o) throws Exception;

	void generateCodeFromStatement(CodeStatement e, TextWriter w, CodeGeneratorOptions o) throws Exception;

	void generateCodeFromNamespace(CodeNamespace e, TextWriter w, CodeGeneratorOptions o)throws  Exception, InvalidOperationException;

	void generateCodeFromCompileUnit(CodeCompileUnit e, TextWriter w, CodeGeneratorOptions o)throws   Exception;;

	void generateCodeFromType(CodeTypeDeclaration e, TextWriter w, CodeGeneratorOptions o) throws Exception, InvalidOperationException;
}