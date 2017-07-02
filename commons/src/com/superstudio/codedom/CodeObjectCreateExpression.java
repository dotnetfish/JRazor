package com.superstudio.codedom;
import java.io.Serializable;
import java.util.ArrayList;
public class CodeObjectCreateExpression extends CodeExpression implements Serializable
{
	private CodeTypeReference createType;

	private CodeExpressionCollection parameters =   new CodeExpressionCollection();

	public final CodeTypeReference getCreateType()
	{
		if (this.createType == null)
		{
			this.createType = new CodeTypeReference("");
		}
		return this.createType;
	}
	public final void setCreateType(CodeTypeReference value)
	{
		this.createType = value;
	}

	public final CodeExpressionCollection getParameters()
	{
		return this.getParameters();
	}

	public CodeObjectCreateExpression()
	{
	}

	public CodeObjectCreateExpression(CodeTypeReference createType, CodeExpression... parameters)
	{
		this.setCreateType(createType);
		this.getParameters().AddRange(parameters);
	}

	public CodeObjectCreateExpression(String createType, CodeExpression... parameters)
	{
		this.setCreateType(new CodeTypeReference(createType));
		this.getParameters().AddRange(parameters);
	}

	public CodeObjectCreateExpression(java.lang.Class createType, CodeExpression... parameters)
	{
		this.setCreateType(new CodeTypeReference(createType));
		this.getParameters().AddRange(parameters);
	}
}