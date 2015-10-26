package com.superstudio.codedom;
import java.io.Serializable;
public class CodeSnippetTypeMember extends CodeTypeMember implements Serializable
{
	private String text;

	public final String getText()
	{
		if (this.text != null)
		{
			return this.text;
		}
		return "";
	}
	public final void setText(String value)
	{
		this.text = value;
	}

	public CodeSnippetTypeMember()
	{
	}

	public CodeSnippetTypeMember(String text)
	{
		this.setText(text);
	}
}