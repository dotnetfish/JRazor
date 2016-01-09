package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeEventReferenceExpression : CodeExpression
public class CodeEventReferenceExpression extends CodeExpression implements Serializable
{
	private CodeExpression targetObject;

	private String eventName;

	public final CodeExpression getTargetObject()
	{
		return this.targetObject;
	}
	public final void setTargetObject(CodeExpression value)
	{
		this.targetObject = value;
	}

	public final String getEventName()
	{
		if (this.eventName != null)
		{
			return this.eventName;
		}
		return "";
	}
	public final void setEventName(String value)
	{
		this.eventName = value;
	}

	public CodeEventReferenceExpression()
	{
	}

	public CodeEventReferenceExpression(CodeExpression targetObject, String eventName)
	{
		this.targetObject = targetObject;
		this.eventName = eventName;
	}
}