package com.superstudio.codedom;

import java.io.Serializable;

public class CodeExpressionCollection extends CollectionBase<CodeExpression> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6746544984516483002L;

	public final CodeExpression getItem(int index)
	{
		return (CodeExpression)get(index);
	}
	public final void setItem(int index, CodeExpression value)
	{
		add(index,value);
	}

	public CodeExpressionCollection()
	{
	}

	public CodeExpressionCollection(CodeExpressionCollection value)
	{
		this.AddRange(value);
	}

	public CodeExpressionCollection(CodeExpression[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeExpression value)
	{
		add(value);return size();
	}

	public final void AddRange(CodeExpression[] value)
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

	public final void AddRange(CodeExpressionCollection value)
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

	public final boolean Contains(CodeExpression value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeExpression[] array, int index)
	{
		copyTo(array,index);
	}

	/*public final int indexOf(CodeExpression value)
	{
		return super.List.indexOf(value);
	}*/

	public final void Insert(int index, CodeExpression value)
	{
		add(index,value);
	}

	public final void Remove(CodeExpression value)
	{
		remove(value);
	}
}