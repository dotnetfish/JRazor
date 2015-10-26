package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeComment : CodeObject
public class CodeComment extends CodeObject implements Serializable
{
	private String text;

	private boolean docComment;

	public final boolean getDocComment()
	{
		return this.docComment;
	}
	public final void setDocComment(boolean value)
	{
		this.docComment = value;
	}

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

	public CodeComment()
	{
	}

	public CodeComment(String text)
	{
		this.setText(text);
	}

	public CodeComment(String text, boolean docComment)
	{
		this.setText(text);
		this.docComment = docComment;
	}
}