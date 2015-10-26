package com.superstudio.codedom;

import java.io.Serializable;

 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeArrayIndexerExpression : CodeExpression
public class CodeArrayIndexerExpression extends CodeExpression implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2677824660460099405L;

	private CodeExpression targetObject;

	private CodeExpressionCollection indices;

	public final CodeExpression getTargetObject()
	{
		return this.targetObject;
	}
	public final void setTargetObject(CodeExpression value)
	{
		this.targetObject = value;
	}

	public final CodeExpressionCollection getIndices()
	{
		if (this.indices == null)
		{
			this.indices =   new CodeExpressionCollection();
		}
		return this.indices;
	}

	public CodeArrayIndexerExpression()
	{
	}

	public CodeArrayIndexerExpression(CodeExpression targetObject, CodeExpression... indices)
	{
		this.targetObject = targetObject;
		this.indices =   new CodeExpressionCollection();
		this.indices.AddRange(indices);
	}
}