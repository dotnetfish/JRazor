package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeThrowExceptionStatement : CodeStatement
public class CodeThrowExceptionStatement extends CodeStatement implements Serializable
{
	private CodeExpression toThrow;

	public final CodeExpression getToThrow()
	{
		return this.toThrow;
	}
	public final void setToThrow(CodeExpression value)
	{
		this.toThrow = value;
	}

	public CodeThrowExceptionStatement()
	{
	}

	public CodeThrowExceptionStatement(CodeExpression toThrow)
	{
		this.setToThrow(toThrow);
	}
}