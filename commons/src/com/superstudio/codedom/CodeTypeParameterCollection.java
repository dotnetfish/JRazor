package com.superstudio.codedom;

import java.io.Serializable;

public class CodeTypeParameterCollection extends CollectionBase implements Serializable
{
	public final CodeTypeParameter getItem(int index)
	{
		return (CodeTypeParameter)get(index);
	}
	public final void setItem(int index, CodeTypeParameter value)
	{
		add(index,value);
	}

	public CodeTypeParameterCollection()
	{
	}

	public CodeTypeParameterCollection(CodeTypeParameterCollection value)
	{
		this.AddRange(value);
	}

	public CodeTypeParameterCollection(CodeTypeParameter[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeTypeParameter value)
	{
		add(value);return size();
	}

	public final void Add(String value)
	{
		this.Add(new CodeTypeParameter(value));
	}

	public final void AddRange(CodeTypeParameter[] value)
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

	public final void AddRange(CodeTypeParameterCollection value)
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

	public final boolean Contains(CodeTypeParameter value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeTypeParameter[] array, int index)
	{
		copyTo(array,index);
	}

/*	public final int indexOf(CodeTypeParameter value)
	{
		return super.List.indexOf(value);
	}
*/
	public final void Insert(int index, CodeTypeParameter value)
	{
		add(index,value);
	}

	public final void Remove(CodeTypeParameter value)
	{
		remove(value);
	}
}