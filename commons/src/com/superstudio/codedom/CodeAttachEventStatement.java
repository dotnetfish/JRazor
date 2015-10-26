package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeAttachEventStatement : CodeStatement
public class CodeAttachEventStatement extends CodeStatement implements Serializable
{
	private CodeEventReferenceExpression eventRef;

	private CodeExpression listener;

	public final CodeEventReferenceExpression getEvent()
	{
		if (this.eventRef == null)
		{
			return new CodeEventReferenceExpression();
		}
		return this.eventRef;
	}
	public final void setEvent(CodeEventReferenceExpression value)
	{
		this.eventRef = value;
	}

	public final CodeExpression getListener()
	{
		return this.listener;
	}
	public final void setListener(CodeExpression value)
	{
		this.listener = value;
	}

	public CodeAttachEventStatement()
	{
	}

	public CodeAttachEventStatement(CodeEventReferenceExpression eventRef, CodeExpression listener)
	{
		this.eventRef = eventRef;
		this.listener = listener;
	}

	public CodeAttachEventStatement(CodeExpression targetObject, String eventName, CodeExpression listener)
	{
		this.eventRef = new CodeEventReferenceExpression(targetObject, eventName);
		this.listener = listener;
	}
}