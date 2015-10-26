package com.superstudio.codedom;

import java.io.Serializable;

import com.superstudio.commons.EmptyEventArgs;
import com.superstudio.commons.EventArgs;
import com.superstudio.commons.EventHandler;
import com.superstudio.commons.SerializationInfo;

public class CodeNamespace extends CodeObject implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6298786789200420703L;

	private String name;

	private CodeNamespaceImportCollection imports = new CodeNamespaceImportCollection();

	private CodeCommentStatementCollection comments = new CodeCommentStatementCollection();

	private CodeTypeDeclarationCollection classes = new CodeTypeDeclarationCollection();

	private CodeNamespaceCollection namespaces = new CodeNamespaceCollection();

	private int populated;

	private static final int ImportsCollection = 1;

	private static final int CommentsCollection = 2;

	private static final int TypesCollection = 4;

		private EventHandler<EmptyEventArgs> populateComments;;

private EventHandler<EmptyEventArgs> populateImports;

private EventHandler<EmptyEventArgs> populateTypes;

	public final CodeTypeDeclarationCollection getTypes()
	{
		if ((this.populated & 4) == 0)
		{
			this.populated |= 4;
			if (this.getPopulateTypes() != null)
			{
				this.getPopulateTypes().execute(this, EventArgs.Empty);
			}
		}
		return this.classes;
	}

	public final CodeNamespaceImportCollection getImports()
	{
		if ((this.populated & 1) == 0)
		{
			this.populated |= 1;
			if (this.getPopulateImports() != null)
			{
				this.getPopulateImports().execute(this, EventArgs.Empty);
			}
		}
		return this.imports;
	}

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

	public final CodeCommentStatementCollection getComments()
	{
		if ((this.populated & 2) == 0)
		{
			this.populated |= 2;
			if (this.getPopulateComments()!=null)
			{
				this.getPopulateComments().execute(this, EventArgs.Empty);
			}
		}
		return this.comments;
	}

	public CodeNamespace()
	{
	}

	public CodeNamespace(String name)
	{
		this.setName(name);
	}

	private CodeNamespace(SerializationInfo info, StreamingContext context)
	{
	}

	public EventHandler<EmptyEventArgs> getPopulateComments() {
		return populateComments;
	}

	public void setPopulateComments(EventHandler<EmptyEventArgs> populateComments) {
		this.populateComments = populateComments;
	}

	public EventHandler<EmptyEventArgs> getPopulateImports() {
		return populateImports;
	}

	public void setPopulateImports(EventHandler<EmptyEventArgs> populateImports) {
		this.populateImports = populateImports;
	}

	public EventHandler<EmptyEventArgs> getPopulateTypes() {
		return populateTypes;
	}

	public void setPopulateTypes(EventHandler<EmptyEventArgs> populateTypes) {
		this.populateTypes = populateTypes;
	}
}