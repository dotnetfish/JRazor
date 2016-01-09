package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeSnippetStatement : CodeStatement
public class CodeSnippetStatement extends CodeStatement implements Serializable
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

	public CodeSnippetStatement()
	{
	}

	public CodeSnippetStatement(String value)
	{
		this.setValue(value);
	}
}