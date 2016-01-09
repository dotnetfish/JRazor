package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeFieldReferenceExpression : CodeExpression
public class CodeFieldReferenceExpression extends CodeExpression implements Serializable
{
	private CodeExpression targetObject;

	private String fieldName;

	public final CodeExpression getTargetObject()
	{
		return this.targetObject;
	}
	public final void setTargetObject(CodeExpression value)
	{
		this.targetObject = value;
	}

	public final String getFieldName()
	{
		if (this.fieldName != null)
		{
			return this.fieldName;
		}
		return "";
	}
	public final void setFieldName(String value)
	{
		this.fieldName = value;
	}

	public CodeFieldReferenceExpression()
	{
	}

	public CodeFieldReferenceExpression(CodeExpression targetObject, String fieldName)
	{
		this.setTargetObject(targetObject);
		this.setFieldName(fieldName);
	}
}