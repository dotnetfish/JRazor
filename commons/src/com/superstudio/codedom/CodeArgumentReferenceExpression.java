package com.superstudio.codedom;

import java.io.Serializable;

 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeArgumentReferenceExpression : CodeExpression
public class CodeArgumentReferenceExpression extends CodeExpression implements Serializable
{
	private String parameterName;

	public final String getParameterName()
	{
		if (this.parameterName != null)
		{
			return this.parameterName;
		}
		return "";
	}
	public final void setParameterName(String value)
	{
		this.parameterName = value;
	}

	public CodeArgumentReferenceExpression()
	{
	}

	public CodeArgumentReferenceExpression(String parameterName)
	{
		this.parameterName = parameterName;
	}
}