package com.superstudio.codedom;

import java.io.Serializable;

public class CodeTypeMemberCollection extends CollectionBase implements Serializable
{
	public final CodeTypeMember getItem(int index)
	{
		return (CodeTypeMember)get(index);
	}
	public final void setItem(int index, CodeTypeMember value)
	{
		add(index,value);
	}

	public CodeTypeMemberCollection()
	{
	}

	public CodeTypeMemberCollection(CodeTypeMemberCollection value)
	{
		this.AddRange(value);
	}

	public CodeTypeMemberCollection(CodeTypeMember[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeTypeMember value)
	{
		add(value);return size();
	}

	public final void AddRange(CodeTypeMember[] value)
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

	public final void AddRange(CodeTypeMemberCollection value)
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

	public final boolean Contains(CodeTypeMember value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeTypeMember[] array, int index)
	{
		copyTo(array,index);
	}

/*	public final int indexOf(CodeTypeMember value)
	{
		return super.List.indexOf(value);
	}
*/
	public final void Insert(int index, CodeTypeMember value)
	{
		add(index,value);
	}

	public final void Remove(CodeTypeMember value)
	{
		remove(value);
	}
	
	
}