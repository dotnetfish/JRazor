package com.superstudio.codedom;

import java.io.Serializable;

 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeNamespaceCollection : CollectionBase
public class CodeNamespaceCollection extends CollectionBase<CodeNamespace> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8024467959258041040L;

	public final CodeNamespace getItem(int index)
	{
		return (CodeNamespace)get(index);
	}
	public final void setItem(int index, CodeNamespace value)
	{
		add(index,value);
	}

	public CodeNamespaceCollection()
	{
	}

	public CodeNamespaceCollection(CodeNamespaceCollection value)
	{
		this.AddRange(value);
	}

	public CodeNamespaceCollection(CodeNamespace[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeNamespace value)
	{
		add(value);return size();
	}

	public final void AddRange(CodeNamespace[] value)
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

	public final void AddRange(CodeNamespaceCollection value)
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

	public final boolean Contains(CodeNamespace value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeNamespace[] array, int index)
	{
		copyTo(array,index);
	}

	/*public final int indexOf(CodeNamespace value)
	{
		return super.List.indexOf(value);
	}*/

	public final void Insert(int index, CodeNamespace value)
	{
		add(index,value);
	}

	public final void Remove(CodeNamespace value)
	{
		remove(value);
	}
}