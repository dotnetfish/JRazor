package com.superstudio.codedom;
import java.io.Serializable;

public class CodeMethodReturnStatement extends CodeStatement implements Serializable
{
	private CodeExpression expression;

	public final CodeExpression getExpression()
	{
		return this.expression;
	}
	public final void setExpression(CodeExpression value)
	{
		this.expression = value;
	}

	public CodeMethodReturnStatement()
	{
	}

	public CodeMethodReturnStatement(CodeExpression expression)
	{
		this.setExpression(expression);
	}
}