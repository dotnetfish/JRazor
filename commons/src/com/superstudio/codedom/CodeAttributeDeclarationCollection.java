package com.superstudio.codedom;

import java.io.Serializable;

 
public class CodeAttributeDeclarationCollection extends CollectionBase<CodeAttributeDeclaration> implements Serializable
{
	public final CodeAttributeDeclaration getItem(int index)
	{
		return (CodeAttributeDeclaration)get(index);
	}
	public final void setItem(int index, CodeAttributeDeclaration value)
	{
		add(index,value);
	}

	public CodeAttributeDeclarationCollection()
	{
	}

	public CodeAttributeDeclarationCollection(CodeAttributeDeclarationCollection value)
	{
		this.AddRange(value);
	}

	public CodeAttributeDeclarationCollection(CodeAttributeDeclaration[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeAttributeDeclaration value)
	{
		add(value);return size();
	}

	public final void AddRange(CodeAttributeDeclaration[] value)
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

	public final void AddRange(CodeAttributeDeclarationCollection value)
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

	public final boolean Contains(CodeAttributeDeclaration value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeAttributeDeclaration[] array, int index)
	{
		copyTo(array,index);
	}

	/*public final int indexOf(CodeAttributeDeclaration value)
	{
		return super.List.indexOf(value);
	}*/

	public final void Insert(int index, CodeAttributeDeclaration value)
	{
		add(index,value);
	}

	public final void Remove(CodeAttributeDeclaration value)
	{
		remove(value);
	}
}