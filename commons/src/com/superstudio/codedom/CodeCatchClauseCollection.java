package com.superstudio.codedom;

import java.io.Serializable;

public class CodeCatchClauseCollection extends CollectionBase implements Serializable
{
	public final CodeCatchClause getItem(int index)
	{
		return (CodeCatchClause)get(index);
	}
	public final void setItem(int index, CodeCatchClause value)
	{
		add(index,value);
	}

	public CodeCatchClauseCollection()
	{
	}

	public CodeCatchClauseCollection(CodeCatchClauseCollection value)
	{
		this.AddRange(value);
	}

	public CodeCatchClauseCollection(CodeCatchClause[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeCatchClause value)
	{
		add(value);return size();
	}

	public final void AddRange(CodeCatchClause[] value)
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

	public final void AddRange(CodeCatchClauseCollection value)
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

	public final boolean Contains(CodeCatchClause value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeCatchClause[] array, int index)
	{
		copyTo(array,index);
	}

/*	public final int indexOf(CodeCatchClause value)
	{
		return super.List.indexOf(value);
	}*/

	public final void Insert(int index, CodeCatchClause value)
	{
		add(index,value);
	}

	public final void Remove(CodeCatchClause value)
	{
		remove(value);
	}
}