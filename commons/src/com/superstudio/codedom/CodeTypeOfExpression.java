package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeTypeOfExpression : CodeExpression
public class CodeTypeOfExpression extends CodeExpression implements Serializable
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

	public CodeTypeOfExpression()
	{
	}

	public CodeTypeOfExpression(CodeTypeReference type)
	{
		this.setType(type);
	}

	public CodeTypeOfExpression(String type)
	{
		this.setType(new CodeTypeReference(type));
	}

	public CodeTypeOfExpression(java.lang.Class type)
	{
		this.setType(new CodeTypeReference(type));
	}
}