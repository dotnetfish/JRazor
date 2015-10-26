package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeMethodInvokeExpression : CodeExpression
public class CodeMethodInvokeExpression extends CodeExpression implements Serializable
{
	private CodeMethodReferenceExpression method;

	private CodeExpressionCollection parameters =   new CodeExpressionCollection();

	public final CodeMethodReferenceExpression getMethod()
	{
		if (this.method == null)
		{
			this.method = new CodeMethodReferenceExpression();
		}
		return this.method;
	}
	public final void setMethod(CodeMethodReferenceExpression value)
	{
		this.method = value;
	}

	public final CodeExpressionCollection getParameters()
	{
		return this.getParameters();
	}

	public CodeMethodInvokeExpression()
	{
	}

	public CodeMethodInvokeExpression(CodeMethodReferenceExpression method, CodeExpression... parameters)
	{
		this.method = method;
		this.getParameters().AddRange(parameters);
	}

	public CodeMethodInvokeExpression(CodeExpression targetObject, String methodName, CodeExpression... parameters)
	{
		this.method = new CodeMethodReferenceExpression(targetObject, methodName);
		this.getParameters().AddRange(parameters);
	}
}