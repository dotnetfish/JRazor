package com.superstudio.codedom;


import com.superstudio.commons.EventArgs;
import com.superstudio.commons.EventListener;

import java.io.Serializable;

public class CodeMemberMethod extends CodeTypeMember implements Serializable
{
	private CodeParameterDeclarationExpressionCollection parameters = new CodeParameterDeclarationExpressionCollection();

	private CodeStatementCollection statements = new CodeStatementCollection();

	private CodeTypeReference returnType;

	private CodeTypeReference privateImplements;

	private CodeTypeReferenceCollection implementationTypes;

	private CodeAttributeDeclarationCollection returnAttributes;

	private CodeTypeParameterCollection typeParameters;

	private CodeExceptionCollection exceptionTypes;

	private int populated;

	private static final int ParametersCollection = 1;

	private static final int StatementsCollection = 2;

	private static final int ImplTypesCollection = 4;



	private EventListener<EventArgs.EmptyEventArgs> populateParameters;


	private EventListener<EventArgs.EmptyEventArgs> populateStatements;


	private EventListener<EventArgs.EmptyEventArgs> populateImplementationTypes;

	public final CodeTypeReference getReturnType()
	{
		if (this.returnType == null)
		{
			this.returnType = new CodeTypeReference(void.class.getName());
		}
		return this.returnType;
	}
	public final void setReturnType(CodeTypeReference value)
	{
		this.returnType = value;
	}

	public final CodeStatementCollection getStatements()
	{
		if ((this.populated & 2) == 0)
		{
			this.populated |= 2;
			if (this.populateStatements != null)
			{
				this.populateStatements.execute(this, EventArgs.Empty);
			}
		}
		return this.statements;
	}

	public final CodeParameterDeclarationExpressionCollection getParameters()
	{
		if ((this.populated & 1) == 0)
		{
			this.populated |= 1;
			if (this.getPopulateParameters() != null)
			{
				this.getPopulateParameters().execute(this, EventArgs.Empty);
			}
		}
		return this.parameters;
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
		if ((this.populated & 4) == 0)
		{
			this.populated |= 4;
			if (this.getPopulateImplementationTypes() != null)
			{
				this.getPopulateImplementationTypes().execute(this, EventArgs.Empty);
			}
		}
		return this.implementationTypes;
	}

	public final CodeAttributeDeclarationCollection getReturnTypeCustomAttributes()
	{
		if (this.returnAttributes == null)
		{
			this.returnAttributes = new CodeAttributeDeclarationCollection();
		}
		return this.returnAttributes;
	}
	public final CodeExceptionCollection getExceptionTypes()
	{
		if (this.exceptionTypes == null)
		{
			this.exceptionTypes=new CodeExceptionCollection() ;
		}
		return this.exceptionTypes;
	}
	public final CodeTypeParameterCollection getTypeParameters()
	{
		if (this.typeParameters == null)
		{
			this.typeParameters=new CodeTypeParameterCollection() ;
		}
		return this.typeParameters;
	}
	public EventListener<EventArgs.EmptyEventArgs> getPopulateImplementationTypes() {
		return populateImplementationTypes;
	}
	public void setPopulateImplementationTypes(EventListener<EventArgs.EmptyEventArgs> populateImplementationTypes) {
		this.populateImplementationTypes = populateImplementationTypes;
	}
	public EventListener<EventArgs.EmptyEventArgs> getPopulateParameters() {
		return populateParameters;
	}
	public void setPopulateParameters(EventListener<EventArgs.EmptyEventArgs> populateParameters) {
		this.populateParameters = populateParameters;
	}
}