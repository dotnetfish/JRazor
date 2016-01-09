package com.superstudio.codedom;
import java.io.Serializable;
public class CodeDirectionExpression extends CodeExpression implements Serializable
{
	private CodeExpression expression;

	private FieldDirection direction = FieldDirection.values()[0];

	public final CodeExpression getExpression()
	{
		return this.expression;
	}
	public final void setExpression(CodeExpression value)
	{
		this.expression = value;
	}

	public final FieldDirection getDirection()
	{
		return this.direction;
	}
	public final void setDirection(FieldDirection value)
	{
		this.direction = value;
	}

	public CodeDirectionExpression()
	{
	}

	public CodeDirectionExpression(FieldDirection direction, CodeExpression expression)
	{
		this.expression = expression;
		this.direction = direction;
	}
}