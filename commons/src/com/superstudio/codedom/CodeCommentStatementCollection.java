package com.superstudio.codedom;

import java.io.Serializable;

public class CodeCommentStatementCollection extends CollectionBase implements Serializable
{
	public final CodeCommentStatement getItem(int index)
	{
		return (CodeCommentStatement)get(index);
	}
	public final void setItem(int index, CodeCommentStatement value)
	{
		add(index,value);
	}

	public CodeCommentStatementCollection()
	{
	}

	public CodeCommentStatementCollection(CodeCommentStatementCollection value)
	{
		this.AddRange(value);
	}

	public CodeCommentStatementCollection(CodeCommentStatement[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeCommentStatement value)
	{
		add(value);return size();
	}

	public final void AddRange(CodeCommentStatement[] value)
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

	public final void AddRange(CodeCommentStatementCollection value)
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

	public final boolean Contains(CodeCommentStatement value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeCommentStatement[] array, int index)
	{
		copyTo(array,index);
	}

	/*public final int indexOf(CodeCommentStatement value)
	{
		return super.List.indexOf(value);
	}
*/
	public final void Insert(int index, CodeCommentStatement value)
	{
		add(index,value);
	}

	public final void Remove(CodeCommentStatement value)
	{
		remove(value);
	}
}