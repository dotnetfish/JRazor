package com.superstudio.codedom;
import java.io.Serializable;

import com.superstudio.commons.csharpbridge.StringHelper;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeGotoStatement : CodeStatement
public class CodeGotoStatement extends CodeStatement implements Serializable
{
	private String label;

	public final String getLabel()
	{
		return this.label;
	}
	public final void setLabel(String value)
	{
		if (StringHelper.isNullOrEmpty(value))
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