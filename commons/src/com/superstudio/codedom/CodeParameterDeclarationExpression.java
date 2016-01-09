package com.superstudio.codedom;
public class CodeParameterDeclarationExpression extends CodeExpression
{
	private CodeTypeReference type;

	private String name;

	private CodeAttributeDeclarationCollection customAttributes;

	private FieldDirection dir = FieldDirection.values()[0];

	public final CodeAttributeDeclarationCollection getCustomAttributes()
	{
		if (this.customAttributes == null)
		{
			this.customAttributes = new CodeAttributeDeclarationCollection();
		}
		return this.customAttributes;
	}
	public final void setCustomAttributes(CodeAttributeDeclarationCollection value)
	{
		this.customAttributes = value;
	}

	public final FieldDirection getDirection()
	{
		return this.dir;
	}
	public final void setDirection(FieldDirection value)
	{
		this.dir = value;
	}

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

	public final String getName()
	{
		if (this.name != null)
		{
			return this.name;
		}
		return "";
	}
	public final void setName(String value)
	{
		this.name = value;
	}

	public CodeParameterDeclarationExpression()
	{
	}

	public CodeParameterDeclarationExpression(CodeTypeReference type, String name)
	{
		this.setType(type);
		this.setName(name);
	}

	public CodeParameterDeclarationExpression(String type, String name)
	{
		this.setType(new CodeTypeReference(type));
		this.setName(name);
	}

	public CodeParameterDeclarationExpression(java.lang.Class type, String name)
	{
		this.setType(new CodeTypeReference(type));
		this.setName(name);
	}
}