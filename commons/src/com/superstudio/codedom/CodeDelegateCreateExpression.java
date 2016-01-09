package com.superstudio.codedom;
import java.io.Serializable;

public class CodeDelegateCreateExpression extends CodeExpression implements Serializable
{
	private CodeTypeReference delegateType;

	private CodeExpression targetObject;

	private String methodName;

	public final CodeTypeReference getDelegateType()
	{
		if (this.delegateType == null)
		{
			this.delegateType = new CodeTypeReference("");
		}
		return this.delegateType;
	}
	public final void setDelegateType(CodeTypeReference value)
	{
		this.delegateType = value;
	}

	public final CodeExpression getTargetObject()
	{
		return this.targetObject;
	}
	public final void setTargetObject(CodeExpression value)
	{
		this.targetObject = value;
	}

	public final String getMethodName()
	{
		if (this.methodName != null)
		{
			return this.methodName;
		}
		return "";
	}
	public final void setMethodName(String value)
	{
		this.methodName = value;
	}

	public CodeDelegateCreateExpression()
	{
	}

	public CodeDelegateCreateExpression(CodeTypeReference delegateType, CodeExpression targetObject, String methodName)
	{
		this.delegateType = delegateType;
		this.targetObject = targetObject;
		this.methodName = methodName;
	}
}