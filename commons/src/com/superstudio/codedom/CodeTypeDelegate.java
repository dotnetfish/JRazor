package com.superstudio.codedom;
import java.io.Serializable;

import com.superstudio.commons.TypeAttributes;

public class CodeTypeDelegate extends CodeTypeDeclaration implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5998427938205809056L;

	private CodeParameterDeclarationExpressionCollection parameters = new CodeParameterDeclarationExpressionCollection();

	private CodeTypeReference returnType;

	public final CodeTypeReference getReturnType()
	{
		if (this.returnType == null)
		{
			this.returnType = new CodeTypeReference("");
		}
		return this.returnType;
	}
	public final void setReturnType(CodeTypeReference value)
	{
		this.returnType = value;
	}

	public final CodeParameterDeclarationExpressionCollection getParameters()
	{
		return this.getParameters();
	}

	public CodeTypeDelegate()
	{
		super.setTypeAttributes(TypeAttributes.forValue(super.getTypeAttributes().getValue() & ~(TypeAttributes.ClassSemanticsMask)));
		super.setTypeAttributes(TypeAttributes.forValue(super.getTypeAttributes().getValue() | TypeAttributes.NotPublic));
		super.getBaseTypes().Clear();
		super.getBaseTypes().Add(new CodeTypeReference("System.Delegate"));
	}

	public CodeTypeDelegate(String name)
	{
		this();
		setName(name);
	}
}