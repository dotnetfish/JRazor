package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeTypeReferenceExpression : CodeExpression
public class CodeTypeReferenceExpression extends CodeExpression implements Serializable
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

	public CodeTypeReferenceExpression()
	{
	}

	public CodeTypeReferenceExpression(CodeTypeReference type)
	{
		this.setType(type);
	}

	public CodeTypeReferenceExpression(String type)
	{
		this.setType(new CodeTypeReference(type));
	}

	public CodeTypeReferenceExpression(java.lang.Class type)
	{
		this.setType(new CodeTypeReference(type));
	}
}