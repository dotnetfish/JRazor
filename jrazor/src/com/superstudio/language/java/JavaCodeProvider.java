package com.superstudio.language.java;

import java.util.Map;

import com.superstudio.codedom.CodeTypeMember;
import com.superstudio.codedom.MemberAttributes;
import com.superstudio.codedom.compiler.CodeDomProvider;
import com.superstudio.codedom.compiler.CodeGeneratorOptions;
import com.superstudio.codedom.compiler.ICodeCompiler;
import com.superstudio.codedom.compiler.ICodeGenerator;
import com.superstudio.commons.TypeAttributes;
import com.superstudio.commons.TypeConverter;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.io.TextWriter;

public class JavaCodeProvider extends CodeDomProvider
{
	private JavaCodeGenerator generator;
	
	@Override
	public  String getFileExtension()
	{
		
			return "java";
		
	}
	
	public JavaCodeProvider()
	{
		this.generator = new JavaCodeGenerator();
	}
	public JavaCodeProvider(Map<String, String> providerOptions) throws ArgumentNullException
	{
		if (providerOptions == null)
		{
			throw new ArgumentNullException("providerOptions");
		}
		this.generator = new JavaCodeGenerator(providerOptions);
	}
	//[Obsolete("Callers should not use the ICodeGenerator interface and should instead use the methods directly on the CodeDomProvider class.")]
	@Override
	public  ICodeGenerator createGenerator()
	{
		return this.generator;
	}
	//[Obsolete("Callers should not use the ICodeCompiler interface and should instead use the methods directly on the CodeDomProvider class.")]
	@Override
	public  ICodeCompiler createCompiler()
	{
		return this.generator;
	}
	@Override
	public  TypeConverter getConverter(Class type)
	{
		if (type == MemberAttributes.class)
		{
			return JavaMemberAttributeConverter.Default();
		}
		if (type == TypeAttributes.class)
		{
			return JavaTypeAttributeConverter.Default();
		}
		return super.getConverter(type);
	}
	@Override
	public  void generateCodeFromMember(CodeTypeMember member, TextWriter writer, CodeGeneratorOptions options) throws Exception
	{
		this.generator.GenerateCodeFromMember(member, writer, options);
	}
}