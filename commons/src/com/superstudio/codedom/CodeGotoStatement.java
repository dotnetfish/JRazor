package com.superstudio.codedom;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class CodeGotoStatement extends CodeStatement implements Serializable
{
	private String label;

	public final String getLabel()
	{
		return this.label;
	}
	public final void setLabel(String value)
	{

		if(StringUtils.isBlank(value))
		{
			throw new IllegalArgumentException("value");
		}
		this.label = value;
	}

	public CodeGotoStatement()
	{
	}

	public CodeGotoStatement(String label)
	{
		this.setLabel(label);
	}
}