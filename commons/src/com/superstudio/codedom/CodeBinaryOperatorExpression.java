package com.superstudio.codedom;
import java.io.Serializable;
public class CodeBinaryOperatorExpression extends CodeExpression implements Serializable
{
	private CodeBinaryOperatorType op = CodeBinaryOperatorType.values()[0];

	private CodeExpression left;

	private CodeExpression right;

	public final CodeExpression getRight()
	{
		return this.right;
	}
	public final void setRight(CodeExpression value)
	{
		this.right = value;
	}

	public final CodeExpression getLeft()
	{
		return this.left;
	}
	public final void setLeft(CodeExpression value)
	{
		this.left = value;
	}

	public final CodeBinaryOperatorType getOperator()
	{
		return this.op;
	}
	public final void setOperator(CodeBinaryOperatorType value)
	{
		this.op = value;
	}

	public CodeBinaryOperatorExpression()
	{
	}

	public CodeBinaryOperatorExpression(CodeExpression left, CodeBinaryOperatorType op, CodeExpression right)
	{
		this.setRight(right);
		this.setOperator(op);
		this.setLeft(left);
	}
}