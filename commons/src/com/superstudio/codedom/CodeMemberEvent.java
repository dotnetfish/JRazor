package com.superstudio.codedom;

import java.io.Serializable;


//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeMemberEvent : CodeTypeMember
public class CodeMemberEvent extends CodeTypeMember implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1917653939623288166L;

	private CodeTypeReference type;

	private CodeTypeReference privateImplements;

	private CodeTypeReferenceCollection implementationTypes;

	public final CodeTypeReference getType()
	{
		if (this.type == null)
		{
			this.type = new CodeTypeReference("");
		}
		return this.type;
	}
	public final void setType(CodeTypeReference value)
	{
		this.type = value;
	}

	public final CodeTypeReference getPrivateImplementationType()
	{
		return this.privateImplements;
	}
	public final void setPrivateImplementationType(CodeTypeReference value)
	{
		this.privateImplements = value;
	}

	public final CodeTypeReferenceCollection getImplementationTypes()
	{
		if (this.implementationTypes == null)
		{
			this.implementationTypes = new CodeTypeReferenceCollection();
		}
		return this.implementationTypes;
	}
}