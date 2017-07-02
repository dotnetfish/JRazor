package com.superstudio.codedom;

import java.io.Serializable;

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
		this.AddRange(value);
	}

	public CodeTypeReferenceCollection(CodeTypeReference[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeTypeReference value)
	{
		add(value);return size();
	}

	public final void Add(String value)
	{
		this.Add(new CodeTypeReference(value));
	}

	public final void Add(java.lang.Class value)
	{
		this.Add(new CodeTypeReference(value));
	}

	public final void AddRange(CodeTypeReference[] value)
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

	public final void AddRange(CodeTypeReferenceCollection value)
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

	public final boolean Contains(CodeTypeReference value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeTypeReference[] array, int index)
	{
		copyTo(array,index);
	}

	/*public final int indexOf(CodeTypeReference value)
	{
		return super.List.indexOf(value);
	}*/

	public final void Insert(int index, CodeTypeReference value)
	{
		add(index,value);
	}

	public final void Remove(CodeTypeReference value)
	{
		remove(value);
	}
}