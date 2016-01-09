package com.superstudio.codedom.compiler;

import com.superstudio.codedom.*;

public class Indentation
{
	private IndentedTextWriter writer;

	private int indent;

	private String s;

	public final String getIndentationString()
	{
		if (this.s == null)
		{
			String tabString = this.writer.getTabString();
			StringBuilder stringBuilder = new StringBuilder(this.indent * tabString.length());
			for (int i = 0; i < this.indent; i++)
			{
				stringBuilder.append(tabString);
			}
			this.s = stringBuilder.toString();
		}
		return this.s;
	}

	public Indentation(IndentedTextWriter writer, int indent)
	{
		this.writer = writer;
		this.indent = indent;
		this.s = null;
	}
}