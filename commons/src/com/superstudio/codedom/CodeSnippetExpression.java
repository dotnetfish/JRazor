package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeSnippetExpression : CodeExpression
public class CodeSnippetExpression extends CodeExpression implements Serializable
{
	private String value;

	public final String getValue()
	{
		if (this.value != null)
		{
			return this.value;
		}
		return "";
	}
	public final void setValue(String value)
	{
		this.value = value;
	}

	public CodeSnippetExpression()
	{
	}

	public CodeSnippetExpression(String value)
	{
		this.setValue(value);
	}
}