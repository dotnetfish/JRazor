package com.superstudio.codedom;

import java.io.Serializable;

 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeDirectiveCollection : CollectionBase
public class CodeDirectiveCollection extends CollectionBase<CodeDirective> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3226414386363863489L;

	public final CodeDirective getItem(int index)
	{
		return (CodeDirective)get(index);
	}
	public final void setItem(int index, CodeDirective value)
	{
		add(index,value);
	}

	public CodeDirectiveCollection()
	{
	}

	public CodeDirectiveCollection(CodeDirectiveCollection value)
	{
		this.AddRange(value);
	}

	public CodeDirectiveCollection(CodeDirective[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeDirective value)
	{
		add(value);return size();
	}

	public final void AddRange(CodeDirective[] value)
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

	public final void AddRange(CodeDirectiveCollection value)
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

	public final boolean Contains(CodeDirective value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeDirective[] array, int index)
	{
		copyTo(array,index);
	}

	/*public final int indexOf(CodeDirective value)
	{
		return super.List.indexOf(value);
	}*/

	public final void Insert(int index, CodeDirective value)
	{
		add(index,value);
	}

	public final void Remove(CodeDirective value)
	{
		remove(value);
	}
}