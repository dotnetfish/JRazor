package com.superstudio.codedom;

import java.io.Serializable;


import com.superstudio.commons.EventArgs;
import com.superstudio.commons.EventListener;

public class CodeNamespace extends CodeObject implements Serializable
{
	private String name;

	private CodeNamespaceImportCollection imports = new CodeNamespaceImportCollection();

	private CodeCommentStatementCollection comments = new CodeCommentStatementCollection();

	private CodeTypeDeclarationCollection classes = new CodeTypeDeclarationCollection();

	private CodeNamespaceCollection namespaces = new CodeNamespaceCollection();

	private int populated;

	private static final int ImportsCollection = 1;

	private static final int CommentsCollection = 2;

	private static final int TypesCollection = 4;

		private EventListener<EventArgs.EmptyEventArgs> populateComments;

	private EventListener<EventArgs.EmptyEventArgs> populateImports;

private EventListener<EventArgs.EmptyEventArgs> populateTypes;

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

	/*private CodeNamespace(SerializationInfo info, StreamingContext context)
	{
	}
*/
	public EventListener<EventArgs.EmptyEventArgs> getPopulateComments() {
		return populateComments;
	}

	public void setPopulateComments(EventListener<EventArgs.EmptyEventArgs> populateComments) {
		this.populateComments = populateComments;
	}

	public EventListener<EventArgs.EmptyEventArgs> getPopulateImports() {
		return populateImports;
	}

	public void setPopulateImports(EventListener<EventArgs.EmptyEventArgs> populateImports) {
		this.populateImports = populateImports;
	}

	public EventListener<EventArgs.EmptyEventArgs> getPopulateTypes() {
		return populateTypes;
	}

	public void setPopulateTypes(EventListener<EventArgs.EmptyEventArgs> populateTypes) {
		this.populateTypes = populateTypes;
	}
}