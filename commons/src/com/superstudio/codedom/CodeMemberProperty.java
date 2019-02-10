package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeMemberProperty : CodeTypeMember
public class CodeMemberProperty extends CodeTypeMember implements Serializable
{
	private CodeTypeReference type;

	private CodeParameterDeclarationExpressionCollection parameters = new CodeParameterDeclarationExpressionCollection();

	private boolean hasGet;

	private boolean hasSet;

	private CodeStatementCollection getStatements = new CodeStatementCollection();

	private CodeStatementCollection setStatements = new CodeStatementCollection();

	private CodeTypeReference privateImplements;

	private CodeTypeReferenceCollection implementationTypes;

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

	public final boolean getHasGet()
	{
		return this.hasGet || this.getStatements.size() > 0;
	}
	public final void setHasGet(boolean value)
	{
		this.hasGet = value;
		if (!value)
		{
			this.getStatements.clear();
		}
	}

	public final boolean getHasSet()
	{
		return this.hasSet || this.setStatements.size() > 0;
	}
	public final void setHasSet(boolean value)
	{
		this.hasSet = value;
		if (!value)
		{
			this.setStatements.clear();
		}
	}

	public final CodeStatementCollection getGetStatements()
	{
		return this.getStatements;
	}

	public final CodeStatementCollection getSetStatements()
	{
		return this.setStatements;
	}

	public final CodeParameterDeclarationExpressionCollection getParameters()
	{
		return this.parameters;
	}
}