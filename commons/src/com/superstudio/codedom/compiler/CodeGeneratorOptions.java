package com.superstudio.codedom.compiler;

import java.util.HashMap;
import java.util.Map;

public class CodeGeneratorOptions
{
	private Map options = new HashMap<String,Object>();

	public final Object getItem(String index)
	{
		return this.options.get(index);
	}
	public final void setItem(String index, Object value)
	{
		this.options.put(index, value);
	}

	public final String getIndentString()
	{
		Object obj = this.options.get("IndentString");
		if (obj != null)
		{
			return (String)obj;
		}
		return "    ";
	}
	public final void setIndentString(String value)
	{
		this.options.put("IndentString", value);
	}

	public final String getBracingStyle()
	{
		Object obj = this.options.get("BracingStyle");
		if (obj != null)
		{
			return (String)obj;
		}
		return "Block";
	}
	public final void setBracingStyle(String value)
	{
		this.options.put("BracingStyle", value);
	}

	public final boolean getElseOnClosing()
	{
		Object obj = this.options.get("ElseOnClosing");
		return obj != null && (Boolean)obj;
	}
	public final void setElseOnClosing(boolean value)
	{
		this.options.put("ElseOnClosing", value);
	}

	public final boolean getBlankLinesBetweenMembers()
	{
		Object obj = this.options.get("BlankLinesBetweenMembers");
		return obj == null || (Boolean)obj;
	}
	public final void setBlankLinesBetweenMembers(boolean value)
	{
		this.options.put("BlankLinesBetweenMembers", value);
	}


//ORIGINAL LINE: [ComVisible(false)] public bool VerbatimOrder
	public final boolean getVerbatimOrder()
	{
		Object obj = this.options.get("VerbatimOrder");
		return obj != null && (Boolean)obj;
	}
	public final void setVerbatimOrder(boolean value)
	{
		this.options.put("VerbatimOrder", value);
	}
}