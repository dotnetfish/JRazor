package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeTypeParameter : CodeObject
public class CodeTypeParameter extends CodeObject implements Serializable
{
	private String name;

	private CodeAttributeDeclarationCollection customAttributes;

	private CodeTypeReferenceCollection constraints;

	private boolean hasConstructorConstraint;

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
	}

	public final CodeTypeReferenceCollection getConstraints()
	{
		if (this.constraints == null)
		{
			this.constraints = new CodeTypeReferenceCollection();
		}
		return this.constraints;
	}

	public final CodeAttributeDeclarationCollection getCustomAttributes()
	{
		if (this.customAttributes == null)
		{
			this.customAttributes = new CodeAttributeDeclarationCollection();
		}
		return this.customAttributes;
	}

	public final boolean getHasConstructorConstraint()
	{
		return this.hasConstructorConstraint;
	}
	public final void setHasConstructorConstraint(boolean value)
	{
		this.hasConstructorConstraint = value;
	}

	public CodeTypeParameter()
	{
	}

	public CodeTypeParameter(String name)
	{
		this.name = name;
	}
}