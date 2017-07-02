package com.superstudio.codedom;

import java.io.Serializable;

public class CodeTypeDeclarationCollection extends CollectionBase implements Serializable
{
	public final CodeTypeDeclaration getItem(int index)
	{
		return (CodeTypeDeclaration)get(index);
	}
	public final void setItem(int index, CodeTypeDeclaration value)
	{
		add(index,value);
	}

	public CodeTypeDeclarationCollection()
	{
	}

	public CodeTypeDeclarationCollection(CodeTypeDeclarationCollection value)
	{
		this.AddRange(value);
	}

	public CodeTypeDeclarationCollection(CodeTypeDeclaration[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeTypeDeclaration value)
	{
		add(value);return size();
	}

	public final void AddRange(CodeTypeDeclaration[] value)
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

	public final void AddRange(CodeTypeDeclarationCollection value)
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

	public final boolean Contains(CodeTypeDeclaration value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeTypeDeclaration[] array, int index)
	{
		copyTo(array,index);
	}

	/*public final int indexOf(CodeTypeDeclaration value)
	{
		return super.List.indexOf(value);
	}*/

	public final void Insert(int index, CodeTypeDeclaration value)
	{
		add(index,value);
	}

	public final void Remove(CodeTypeDeclaration value)
	{
		remove(value);
	}
}