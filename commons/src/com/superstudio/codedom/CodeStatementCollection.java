package com.superstudio.codedom;
import java.io.Serializable;
 
public class CodeStatementCollection extends CollectionBase<CodeStatement> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7534278459818619122L;

	public final CodeStatement getItem(int index)
	{
		return (CodeStatement)get(index);
	}
	public final void setItem(int index, CodeStatement value)
	{
		add(index,value);
	}

	public CodeStatementCollection()
	{
	}

	public CodeStatementCollection(CodeStatementCollection value)
	{
		this.addAll(value);
	}

	public CodeStatementCollection(CodeStatement[] value)
	{
		this.addAll(value);
	}

	public final int Add(CodeStatement value)
	{
		add(value);return size();
	}

	public final int Add(CodeExpression value)
	{
		return this.Add(new CodeExpressionStatement(value));
	}

	public final void addAll(CodeStatement[] value)
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

	public final void addAll(CodeStatementCollection value)
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

	public final boolean Contains(CodeStatement value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeStatement[] array, int index)
	{
		subList(index,size()-index-1).toArray(array);
	}

	/*public final int indexOf(CodeStatement value)
	{
		return super.List.indexOf(value);
	}*/

	public final void Insert(int index, CodeStatement value)
	{
		add(index,value);
	}

	public final void Remove(CodeStatement value)
	{
		remove(value);
	}
	
}