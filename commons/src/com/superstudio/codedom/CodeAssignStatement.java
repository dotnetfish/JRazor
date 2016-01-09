package com.superstudio.codedom;

import java.io.Serializable;


//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeAssignStatement : CodeStatement
public class CodeAssignStatement extends CodeStatement implements Serializable
{
	private CodeExpression left;

	private CodeExpression right;

	public final CodeExpression getLeft()
	{
		return this.left;
	}
	public final void setLeft(CodeExpression value)
	{
		this.left = value;
	}

	public final CodeExpression getRight()
	{
		return this.right;
	}
	public final void setRight(CodeExpression value)
	{
		this.right = value;
	}

	public CodeAssignStatement()
	{
	}

	public CodeAssignStatement(CodeExpression left, CodeExpression right)
	{
		this.setLeft(left);
		this.setRight(right);
	}
}