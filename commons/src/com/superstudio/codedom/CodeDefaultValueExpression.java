package com.superstudio.codedom;
import java.io.Serializable;
public class CodeDefaultValueExpression extends CodeExpression implements Serializable
{
	private CodeTypeReference type;

	public final CodeTypeReference getType()
	{
		if (this.type == null)
		{
			this.type = new CodeTypeReference("");
		}
		return this.type;
	}
	public final void setType(CodeTypeReference value)
	{
		this.type = value;
	}

	public CodeDefaultValueExpression()
	{
	}

	public CodeDefaultValueExpression(CodeTypeReference type)
	{
		this.type = type;
	}
}