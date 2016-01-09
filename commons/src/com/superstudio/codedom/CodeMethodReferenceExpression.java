package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeMethodReferenceExpression : CodeExpression
public class CodeMethodReferenceExpression extends CodeExpression implements Serializable
{
	private CodeExpression targetObject;

	private String methodName;


//ORIGINAL LINE: [OptionalField] private CodeTypeReferenceCollection typeArguments;
	private CodeTypeReferenceCollection typeArguments;

	public final CodeExpression getTargetObject()
	{
		return this.targetObject;
	}
	public final void setTargetObject(CodeExpression value)
	{
		this.targetObject = value;
	}

	public final String getMethodName()
	{
		if (this.methodName != null)
		{
			return this.methodName;
		}
		return "";
	}
	public final void setMethodName(String value)
	{
		this.methodName = value;
	}


//ORIGINAL LINE: [ComVisible(false)] public CodeTypeReferenceCollection TypeArguments
	public final CodeTypeReferenceCollection getTypeArguments()
	{
		if (this.typeArguments == null)
		{
			this.typeArguments = new CodeTypeReferenceCollection();
		}
		return this.typeArguments;
	}

	public CodeMethodReferenceExpression()
	{
	}

	public CodeMethodReferenceExpression(CodeExpression targetObject, String methodName)
	{
		this.setTargetObject(targetObject);
		this.setMethodName(methodName);
	}

	public CodeMethodReferenceExpression(CodeExpression targetObject, String methodName, CodeTypeReference... typeParameters)
	{
		this.setTargetObject(targetObject);
		this.setMethodName(methodName);
		if (typeParameters != null && typeParameters.length != 0)
		{
			this.getTypeArguments().AddRange(typeParameters);
		}
	}
}