package com.superstudio.codedom;

import java.io.Serializable;


//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeParameterDeclarationExpressionCollection : CollectionBase
public class CodeParameterDeclarationExpressionCollection extends CollectionBase implements Serializable
{
	public final CodeParameterDeclarationExpression getItem(int index)
	{
		return (CodeParameterDeclarationExpression)get(index);
	}
	public final void setItem(int index, CodeParameterDeclarationExpression value)
	{
		//set[index] = value;
		add(index,value);
	}

	public CodeParameterDeclarationExpressionCollection()
	{
	}

	public CodeParameterDeclarationExpressionCollection(CodeParameterDeclarationExpressionCollection value)
	{
		this.AddRange(value);
	}

	public CodeParameterDeclarationExpressionCollection(CodeParameterDeclarationExpression[] value)
	{
		this.AddRange(value);
	}

	public final int Add(CodeParameterDeclarationExpression value)
	{
		add(value);return size();
		//return super.List.add(value);
	}

	public final void AddRange(CodeParameterDeclarationExpression[] value)
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

	public final void AddRange(CodeParameterDeclarationExpressionCollection value)
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

	public final boolean Contains(CodeParameterDeclarationExpression value)
	{
		return contains(value);
	}

	public final void CopyTo(CodeParameterDeclarationExpression[] array, int index)
	{
		copyTo(array, index);
	}

	/*public final int indexOf(CodeParameterDeclarationExpression value)
	{
		return super.List.indexOf(value);
	}*/

	public final void Insert(int index, CodeParameterDeclarationExpression value)
	{
		//super.List.insert(index, value);
		add(index,value);
	}

	public final void Remove(CodeParameterDeclarationExpression value)
	{
		remove(value);
	}
}