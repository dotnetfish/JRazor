package com.superstudio.codedom;

import java.io.Serializable;

/**
 * @author cloudartisan
 */
public class CodeTypeReferenceCollection extends CollectionBase implements Serializable
{
	public final CodeTypeReference getItem(int index)
	{
		return (CodeTypeReference)get(index);
	}
	public final void setItem(int index, CodeTypeReference value)
	{
		add(index,value);
	}

	public CodeTypeReferenceCollection()
	{
	}

	public CodeTypeReferenceCollection(CodeTypeReferenceCollection value)
	{
		this.addRange(value);
	}

	public CodeTypeReferenceCollection(CodeTypeReference[] value)
	{
		this.addRange(value);
	}

	public final int add(CodeTypeReference value)
	{
		add(value);
		return size();
	}

	public final void add(String value)
	{
		this.add(new CodeTypeReference(value));
	}

	public final void add(java.lang.Class value)
	{
		this.add(new CodeTypeReference(value));
	}

	public final void addRange(CodeTypeReference[] value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("value");
		}
		for (int i = 0; i < value.length; i++)
		{
			this.add(value[i]);
		}

	}

	public final void addRange(CodeTypeReferenceCollection value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("value");
		}
		int count = value.size();
		for (int i = 0; i < count; i++)
		{
			this.add(value.getItem(i));
		}
	}

}