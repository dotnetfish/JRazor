package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeVariableDeclarationStatement : CodeStatement
public class CodeVariableDeclarationStatement extends CodeStatement implements Serializable
{
	private CodeTypeReference type;

	private String name;

	private CodeExpression initExpression;

	public final CodeExpression getInitExpression()
	{
		return this.initExpression;
	}
	public final void setInitExpression(CodeExpression value)
	{
		this.initExpression = value;
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

	public CodeVariableDeclarationStatement()
	{
	}

	public CodeVariableDeclarationStatement(CodeTypeReference type, String name)
	{
		this.setType(type);
		this.setName(name);
	}

	public CodeVariableDeclarationStatement(String type, String name)
	{
		this.setType(new CodeTypeReference(type));
		this.setName(name);
	}

	public CodeVariableDeclarationStatement(java.lang.Class type, String name)
	{
		this.setType(new CodeTypeReference(type));
		this.setName(name);
	}

	public CodeVariableDeclarationStatement(CodeTypeReference type, String name, CodeExpression initExpression)
	{
		this.setType(type);
		this.setName(name);
		this.setInitExpression(initExpression);
	}

	public CodeVariableDeclarationStatement(String type, String name, CodeExpression initExpression)
	{
		this.setType(new CodeTypeReference(type));
		this.setName(name);
		this.setInitExpression(initExpression);
	}

	public CodeVariableDeclarationStatement(java.lang.Class type, String name, CodeExpression initExpression)
	{
		this.setType(new CodeTypeReference(type));
		this.setName(name);
		this.setInitExpression(initExpression);
	}
}