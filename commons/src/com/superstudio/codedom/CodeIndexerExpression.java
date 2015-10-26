package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeIndexerExpression : CodeExpression
public class CodeIndexerExpression extends CodeExpression implements Serializable
{
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

	public CodeIndexerExpression()
	{
	}

	public CodeIndexerExpression(CodeExpression targetObject, CodeExpression... indices)
	{
		this.targetObject = targetObject;
		this.indices =   new CodeExpressionCollection();
		this.indices.AddRange(indices);
	}
}