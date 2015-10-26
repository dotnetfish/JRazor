package com.superstudio.codedom;

import java.io.Serializable;

 
public class CodeAttributeArgumentCollection extends CollectionBase<CodeAttributeArgument> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1019671289576384771L;

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
		this.AddRange(value);
	}

	public CodeAttributeArgumentCollection(CodeAttributeArgument[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeAttributeArgument value)
	{
		add(value);return size();
	}

	public final void AddRange(CodeAttributeArgument[] value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("value");
		}
		for (int i = 0; i < value.length; i++)
		{
			this.Add(value[i]);
		}
	}

	public final void AddRange(CodeAttributeArgumentCollection value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("value");
		}
		int count = value.size();
		for (int i = 0; i < count; i++)
		{
			this.Add(value.getItem(i));
		}
	}

	public final boolean Contains(CodeAttributeArgument value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeAttributeArgument[] array, int index)
	{
		copyTo(array,index);
	}

	/*public final int indexOf(CodeAttributeArgument value)
	{
		return super.List.indexOf(value);
	}*/

	public final void Insert(int index, CodeAttributeArgument value)
	{
		add(index,value);
	}

	public final void Remove(CodeAttributeArgument value)
	{
		remove(value);
	}
}