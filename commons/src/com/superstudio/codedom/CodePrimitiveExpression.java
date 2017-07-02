package com.superstudio.codedom;
import java.io.Serializable;
public class CodePrimitiveExpression extends CodeExpression implements Serializable
{
	private Object value;

	public final Object getValue()
	{
		return this.value;
	}
	public final void setValue(Object value)
	{
		this.value = value;
	}

	public CodePrimitiveExpression()
	{
	}

	public CodePrimitiveExpression(Object value)
	{
		this.setValue(value);
	}
}