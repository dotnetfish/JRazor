package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodePrimitiveExpression : CodeExpression
public class CodePrimitiveExpression extends CodeExpression implements Serializable
{
	private Object value;

	public final Object getValue()
	{
		return this.value;
	}
	public final void setValue(Object value)
	{
		this.value = value;
	}

	public CodePrimitiveExpression()
	{
	}

	public CodePrimitiveExpression(Object value)
	{
		this.setValue(value);
	}
}