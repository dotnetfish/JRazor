package com.superstudio.codedom;

import java.io.Serializable;

public class CodeCommentStatement extends CodeStatement implements Serializable
{
	private CodeComment comment;

	public final CodeComment getComment()
	{
		return this.comment;
	}
	public final void setComment(CodeComment value)
	{
		this.comment = value;
	}

	public CodeCommentStatement()
	{
	}

	public CodeCommentStatement(CodeComment comment)
	{
		this.comment = comment;
	}

	public CodeCommentStatement(String text)
	{
		this.comment = new CodeComment(text);
	}

	public CodeCommentStatement(String text, boolean docComment)
	{
		this.comment = new CodeComment(text, docComment);
	}
}