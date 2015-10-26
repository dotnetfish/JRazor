package com.superstudio.codedom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeCompileUnit : CodeObject
public class CodeCompileUnit extends CodeObject implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6442345025010069436L;

	private CodeNamespaceCollection namespaces = new CodeNamespaceCollection();

	private List<String> assemblies;

	private CodeAttributeDeclarationCollection attributes;

 
//ORIGINAL LINE: [OptionalField] private CodeDirectiveCollection startDirectives;
	private CodeDirectiveCollection startDirectives;

 
//ORIGINAL LINE: [OptionalField] private CodeDirectiveCollection endDirectives;
	private CodeDirectiveCollection endDirectives;

	public final CodeNamespaceCollection getNamespaces()
	{
		return this.namespaces;
	}

	public final List<String> getReferencedAssemblies()
	{
		if (this.assemblies == null)
		{
			this.assemblies = new ArrayList<String>();
		}
		return this.assemblies;
	}

	public final CodeAttributeDeclarationCollection getAssemblyCustomAttributes()
	{
		if (this.attributes == null)
		{
			this.attributes = new CodeAttributeDeclarationCollection();
		}
		return this.attributes;
	}

	public final CodeDirectiveCollection getStartDirectives()
	{
		if (this.startDirectives == null)
		{
			this.startDirectives = new CodeDirectiveCollection();
		}
		return this.startDirectives;
	}

	public final CodeDirectiveCollection getEndDirectives()
	{
		if (this.endDirectives == null)
		{
			this.endDirectives=new CodeDirectiveCollection();
		}
		return this.endDirectives;
	}
}