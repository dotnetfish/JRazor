package com.superstudio.jrazor.generator;


import com.superstudio.jrazor.parser.syntaxTree.*;

public class SetVBOptionCodeGenerator extends SpanCodeGenerator
{
	public static final String StrictCodeDomOptionName = "AllowLateBound";
	public static final String ExplicitCodeDomOptionName = "RequireVariableDeclaration";

	public SetVBOptionCodeGenerator(String optionName, boolean value)
	{
		setOptionName(optionName);
		setValue(value);
	}

	// CodeDOM Option Name, which is NOT the same as the VB Option Name
	private String privateOptionName;
	public final String getOptionName()
	{
		return privateOptionName;
	}
	private void setOptionName(String value)
	{
		privateOptionName = value;
	}
	private boolean privateValue;
	public final boolean getValue()
	{
		return privateValue;
	}
	private void setValue(boolean value)
	{
		privateValue = value;
	}

	public static SetVBOptionCodeGenerator Strict(boolean onOffValue)
	{
		// Strict On = AllowLateBound Off
		return new SetVBOptionCodeGenerator(StrictCodeDomOptionName, !onOffValue);
	}

	public static SetVBOptionCodeGenerator Explicit(boolean onOffValue)
	{
		return new SetVBOptionCodeGenerator(ExplicitCodeDomOptionName, onOffValue);
	}

	@Override
	public void generateCode(Span target, CodeGeneratorContext context)
	{
		context.getCompileUnit().getUserData().put(getOptionName(), getValue());
	}

	@Override
	public String toString()
	{
		return "Option:" + getOptionName() + "=" + getValue();
	}
}