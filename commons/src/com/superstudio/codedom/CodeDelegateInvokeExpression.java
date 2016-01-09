package com.superstudio.codedom;
import java.io.Serializable;

public class CodeDelegateInvokeExpression extends CodeExpression implements Serializable
{
	private CodeExpression targetObject;

	private CodeExpressionCollection parameters =   new CodeExpressionCollection();

	public final CodeExpression getTargetObject()
	{
		return this.targetObject;
	}
	public final void setTargetObject(CodeExpression value)
	{
		this.targetObject = value;
	}

	public final CodeExpressionCollection getParameters()
	{
		return this.getParameters();
	}

	public CodeDelegateInvokeExpression()
	{
	}

	public CodeDelegateInvokeExpression(CodeExpression targetObject)
	{
		this.setTargetObject(targetObject);
	}

	public CodeDelegateInvokeExpression(CodeExpression targetObject, CodeExpression... parameters)
	{
		this.setTargetObject(targetObject);
		this.getParameters().AddRange(parameters);
	}
}