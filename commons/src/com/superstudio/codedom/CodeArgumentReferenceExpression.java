package com.superstudio.codedom;

public class CodeArgumentReferenceExpression extends CodeExpression
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