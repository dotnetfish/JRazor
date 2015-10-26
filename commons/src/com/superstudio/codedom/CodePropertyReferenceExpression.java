package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodePropertyReferenceExpression : CodeExpression
public class CodePropertyReferenceExpression extends CodeExpression implements Serializable
{
	private CodeExpression targetObject;

	private String propertyName;

	private CodeExpressionCollection parameters =   new CodeExpressionCollection();

	public final CodeExpression getTargetObject()
	{
		return this.targetObject;
	}
	public final void setTargetObject(CodeExpression value)
	{
		this.targetObject = value;
	}

	public final String getPropertyName()
	{
		if (this.propertyName != null)
		{
			return this.propertyName;
		}
		return "";
	}
	public final void setPropertyName(String value)
	{
		this.propertyName = value;
	}

	public CodePropertyReferenceExpression()
	{
	}

	public CodePropertyReferenceExpression(CodeExpression targetObject, String propertyName)
	{
		this.setTargetObject(targetObject);
		this.setPropertyName(propertyName);
	}
}