package com.superstudio.codedom;

import java.io.Serializable;

public class CodeAttributeDeclarationCollection extends CollectionBase implements Serializable
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
		this.addRange(value);
	}

	public CodeAttributeDeclarationCollection(CodeAttributeDeclaration[] value)
	{
		this.addRange(value);
	}

	public final int add(CodeAttributeDeclaration value)
	{
		add(value);return size();
	}

	public final void addRange(CodeAttributeDeclaration[] value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("value");
		}
		for (int i = 0; i < value.length; i++)
		{
			this.add(value[i]);
		}
	}

	public final void addRange(CodeAttributeDeclarationCollection value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("value");
		}
		int count = value.size();
		for (int i = 0; i < count; i++)
		{
			this.add(value.getItem(i));
		}
	}

	public final boolean contains(CodeAttributeDeclaration value)
	{
		return contains(value);
	}

	public final void copyTo(CodeAttributeDeclaration[] array, int index)
	{
		copyTo(array,index);
	}

	/*public final int indexOf(CodeAttributeDeclaration value)
	{
		return super.List.indexOf(value);
	}*/

	public final void insert(int index, CodeAttributeDeclaration value)
	{
		add(index,value);
	}

	public final void remove(CodeAttributeDeclaration value)
	{
		remove(value);
	}
}