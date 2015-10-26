package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeMemberField : CodeTypeMember
public class CodeMemberField extends CodeTypeMember implements Serializable
{
	private CodeTypeReference type;

	private CodeExpression initExpression;

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

	public final CodeExpression getInitExpression()
	{
		return this.initExpression;
	}
	public final void setInitExpression(CodeExpression value)
	{
		this.initExpression = value;
	}

	public CodeMemberField()
	{
	}

	public CodeMemberField(CodeTypeReference type, String name)
	{
		this.setType(type);
		setName(name);
	}

	public CodeMemberField(String type, String name)
	{
		this.setType(new CodeTypeReference(type));
		setName(name);
	}

	public CodeMemberField(java.lang.Class type, String name)
	{
		this.setType(new CodeTypeReference(type));
		setName(name);
	}
}