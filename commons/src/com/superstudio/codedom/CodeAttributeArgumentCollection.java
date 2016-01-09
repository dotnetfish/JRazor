package com.superstudio.codedom;

import java.io.Serializable;

public class CodeAttributeArgumentCollection extends CollectionBase implements Serializable
{
	public final CodeAttributeArgument getItem(int index)
	{
		return (CodeAttributeArgument)get(index);
	}
	public final void setItem(int index, CodeAttributeArgument value)
	{
		add(index,value);
	}

	public CodeAttributeArgumentCollection()
	{
	}

	public CodeAttributeArgumentCollection(CodeAttributeArgumentCollection value)
	{
		this.addRange(value);
	}

	public CodeAttributeArgumentCollection(CodeAttributeArgument[] value)
	{
		this.addRange(value);
	}

	public final int add(CodeAttributeArgument value)
	{
		add(value);return size();
	}

	public final void addRange(CodeAttributeArgument[] value)
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

	public final void addRange(CodeAttributeArgumentCollection value)
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

	public final boolean contains(CodeAttributeArgument value)
	{
		return contains(value);
	}

	public final void copyTo(CodeAttributeArgument[] array, int index)
	{
		copyTo(array,index);
	}

	/*public final int indexOf(CodeAttributeArgument value)
	{
		return super.List.indexOf(value);
	}*/

	public final void insert(int index, CodeAttributeArgument value)
	{
		add(index,value);
	}

	public final void remove(CodeAttributeArgument value)
	{
		remove(value);
	}
}