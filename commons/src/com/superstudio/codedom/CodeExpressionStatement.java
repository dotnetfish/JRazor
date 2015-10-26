package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeExpressionStatement : CodeStatement
public class CodeExpressionStatement extends CodeStatement implements Serializable
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

	public CodeExpressionStatement()
	{
	}

	public CodeExpressionStatement(CodeExpression expression)
	{
		this.expression = expression;
	}
}