package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeAttributeDeclaration
public class CodeAttributeDeclaration implements Serializable
{
	private String name;

	private CodeAttributeArgumentCollection arguments = new CodeAttributeArgumentCollection();


//ORIGINAL LINE: [OptionalField] private CodeTypeReference attributeType;
	private CodeTypeReference attributeType;

	public final String getName()
	{
		if (this.name != null)
		{
			return this.name;
		}
		return "";
	}
	public final void setName(String value)
	{
		this.name = value;
		this.attributeType = new CodeTypeReference(this.name);
	}

	public final CodeAttributeArgumentCollection getArguments()
	{
		return this.arguments;
	}

	public final CodeTypeReference getAttributeType()
	{
		return this.attributeType;
	}

	public CodeAttributeDeclaration()
	{
	}

	public CodeAttributeDeclaration(String name)
	{
		this.setName(name);
	}

	public CodeAttributeDeclaration(String name, CodeAttributeArgument... arguments)
	{
		this.setName(name);
		this.getArguments().addRange(arguments);
	}

	public CodeAttributeDeclaration(CodeTypeReference attributeType)
	{
		this(attributeType, null);
	}

	public CodeAttributeDeclaration(CodeTypeReference attributeType, CodeAttributeArgument... arguments)
	{
		this.attributeType = attributeType;
		if (attributeType != null)
		{
			this.name = attributeType.getBaseType();
		}
		if (arguments != null)
		{
			this.getArguments().addRange(arguments);
		}
	}
}