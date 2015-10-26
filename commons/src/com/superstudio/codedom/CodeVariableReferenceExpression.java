package com.superstudio.codedom;

import java.io.Serializable;

 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeVariableReferenceExpression : CodeExpression
public class CodeVariableReferenceExpression extends CodeExpression implements Serializable
{
	private String variableName;

	public final String getVariableName()
	{
		if (this.variableName != null)
		{
			return this.variableName;
		}
		return "";
	}
	public final void setVariableName(String value)
	{
		this.variableName = value;
	}

	public CodeVariableReferenceExpression()
	{
	}

	public CodeVariableReferenceExpression(String variableName)
	{
		this.variableName = variableName;
	}
}