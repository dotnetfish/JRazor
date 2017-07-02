package com.superstudio.codedom.compiler;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;

import com.superstudio.codedom.*;
import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.csharpbridge.StringHelper;

public class CompilerError implements Serializable
{
	private int line;

	private int column;

	private String errorNumber;

	private boolean warning;

	private String errorText;

	private String fileName;

	public final int getLine()
	{
		return this.line;
	}
	public final void setLine(int value)
	{
		this.line = value;
	}

	public final int getColumn()
	{
		return this.column;
	}
	public final void setColumn(int value)
	{
		this.column = value;
	}

	public final String getErrorNumber()
	{
		return this.errorNumber;
	}
	public final void setErrorNumber(String value)
	{
		this.errorNumber = value;
	}

	public final String getErrorText()
	{
		return this.errorText;
	}
	public final void setErrorText(String value)
	{
		this.errorText = value;
	}

	public final boolean getIsWarning()
	{
		return this.warning;
	}
	public final void setIsWarning(boolean value)
	{
		this.warning = value;
	}

	public final String getFileName()
	{
		return this.fileName;
	}
	public final void setFileName(String value)
	{
		this.fileName = value;
	}

	public CompilerError()
	{
		this.line = 0;
		this.column = 0;
		this.errorNumber = "";
		this.errorText = "";
		this.fileName = "";
	}

	public CompilerError(String fileName, int line, int column, String errorNumber, String errorText)
	{
		this.line = line;
		this.column = column;
		this.errorNumber = errorNumber;
		this.errorText = errorText;
		this.fileName = fileName;
	}

	@Override
	public String toString()
	{
		MessageFormat formattor=new MessageFormat("", Locale.getDefault());
		if (this.getFileName().length() > 0)
		{
			return formattor.format("%1$s(%2$s,%3$s) : %4$s %5$s: %6$s", new Object[] {this.getFileName(), this.getLine(), this.getColumn(), this.getIsWarning() ? "warning" : "error", this.getErrorNumber(), this.getErrorText()});
		}
		return formattor.format( "%1$s %2$s: %3$s", new Object[] {this.getIsWarning() ? "warning" : "error", this.getErrorNumber(), this.getErrorText()});
	}
}