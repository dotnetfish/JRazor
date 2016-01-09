package com.superstudio.codedom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeArrayCreateExpression : CodeExpression
public class CodeArrayCreateExpression extends CodeExpression implements Serializable
{
	private CodeTypeReference createType;

	private CodeExpressionCollection initializers =    new CodeExpressionCollection();

	private CodeExpression sizeExpression;

	private int size;

	public final CodeTypeReference getCreateType()
	{
		if (this.createType == null)
		{
			this.createType = new CodeTypeReference("");
		}
		return this.createType;
	}
	public final void setCreateType(CodeTypeReference value)
	{
		this.createType = value;
	}

	public final CodeExpressionCollection getInitializers()
	{
		return this.initializers;
	}

	public final int getSize()
	{
		return this.size;
	}
	public final void setSize(int value)
	{
		this.size = value;
	}

	public final CodeExpression getSizeExpression()
	{
		return this.sizeExpression;
	}
	public final void setSizeExpression(CodeExpression value)
	{
		this.sizeExpression = value;
	}

	public CodeArrayCreateExpression()
	{
	}

	public CodeArrayCreateExpression(CodeTypeReference createType, CodeExpression... initializers)
	{
		this.createType = createType;
		this.initializers.AddRange(initializers);
	}

	public CodeArrayCreateExpression(String createType, CodeExpression... initializers)
	{
		this.createType = new CodeTypeReference(createType);
		this.initializers.AddRange(initializers);
	}

	public CodeArrayCreateExpression(java.lang.Class createType, CodeExpression... initializers)
	{
		this.createType = new CodeTypeReference(createType);
		this.initializers.AddRange(initializers);
	}

	public CodeArrayCreateExpression(CodeTypeReference createType, int size)
	{
		this.createType = createType;
		this.size = size;
	}

	public CodeArrayCreateExpression(String createType, int size)
	{
		this.createType = new CodeTypeReference(createType);
		this.size = size;
	}

	public CodeArrayCreateExpression(java.lang.Class createType, int size)
	{
		this.createType = new CodeTypeReference(createType);
		this.size = size;
	}

	public CodeArrayCreateExpression(CodeTypeReference createType, CodeExpression size)
	{
		this.createType = createType;
		this.sizeExpression = size;
	}

	public CodeArrayCreateExpression(String createType, CodeExpression size)
	{
		this.createType = new CodeTypeReference(createType);
		this.sizeExpression = size;
	}

	public CodeArrayCreateExpression(java.lang.Class createType, CodeExpression size)
	{
		this.createType = new CodeTypeReference(createType);
		this.sizeExpression = size;
	}
}