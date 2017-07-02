package com.superstudio.codedom;
import java.io.Serializable;
public class CodeNamespaceImport extends CodeObject implements Serializable
{
	private String nameSpace;

	private CodeLinePragma linePragma;

	public final CodeLinePragma getLinePragma()
	{
		return this.linePragma;
	}
	public final void setLinePragma(CodeLinePragma value)
	{
		this.linePragma = value;
	}

	public final String getNamespace()
	{
		if (this.nameSpace != null)
		{
			return this.nameSpace;
		}
		return "";
	}
	public final void setNamespace(String value)
	{
		this.nameSpace = value;
	}

	public CodeNamespaceImport()
	{
	}

	public CodeNamespaceImport(String nameSpace)
	{
		this.setNamespace(nameSpace);
	}
}