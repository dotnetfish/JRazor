package com.superstudio.codedom;

import java.io.Serializable;
import java.util.*;

 
 
public class CodeNamespaceImportCollection implements  Iterable<CodeNamespaceImport>, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4888432700163255170L;

	private ArrayList<CodeNamespaceImport> data = new ArrayList<CodeNamespaceImport>();

	private Hashtable<String,CodeNamespaceImport> keys = new Hashtable<String,CodeNamespaceImport>();

	public final CodeNamespaceImport get(int index)
	{
		return (CodeNamespaceImport)this.data.get(index);
	}
	public final void set(int index, CodeNamespaceImport value)
	{
		this.data.set(index, value);
		this.SyncKeys();
	}

	public final int size()
	{
		return this.data.size();
	}

	/*private boolean getIsReadOnly()
	{
		return false;
	}

	private boolean getIsFixedSize()
	{
		return false;
	}*/

	
	

	

	/*private boolean getIsSynchronized()
	{
		return false;
	}

	private Object getSyncRoot()
	{
		return null;
	}
*/
	public final void Add(CodeNamespaceImport value)
	{
		if (!this.keys.containsKey(value.getNamespace()))
		{
			this.keys.put(value.getNamespace(), value);
			this.data.add(value);
		}
	}

	public final void AddRange(CodeNamespaceImport[] value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("value");
		}
		for (int i = 0; i < value.length; i++)
		{
			CodeNamespaceImport value2 = value[i];
			this.Add(value2);
		}
	}

	public final void clear()
	{
		this.data.clear();
		this.keys.clear();
	}

	private void SyncKeys()
	{
		this.keys = new Hashtable<String,CodeNamespaceImport>();
		for (CodeNamespaceImport codeNamespaceImport : this.data)
		{
			this.keys.put(codeNamespaceImport.getNamespace(), codeNamespaceImport);
		}
	}

	public final Iterator<CodeNamespaceImport> iterator()
	{
		return this.data.iterator();
	}

	/*public final void CopyTo(CodeNamespaceImport[] array, int index)
	{
		this.data.copyTo(array, index);
	}*/

	

	public final int Add(Object value)
	{
		if( this.data.add((CodeNamespaceImport)value)){
			return size();
		};
		return -1;
	}

	

	public final boolean contains(Object objectValue)
	{
		Object value = (Object)objectValue;
		return this.data.contains(value);
	}

	public final int indexOf(Object objectValue)
	{
		Object value = (Object)objectValue;
		return this.data.indexOf((CodeNamespaceImport)value);
	}

	public final void add(int index, Object value)
	{
		this.data.add(index, (CodeNamespaceImport)value);
		this.SyncKeys();
	}

	public final void remove(Object objectValue)
	{
		Object value = (Object)objectValue;
		this.data.remove((CodeNamespaceImport)value);
		this.SyncKeys();
	}

	public final void remove(int index)
	{
		this.data.remove(index);
		this.SyncKeys();
	}
}