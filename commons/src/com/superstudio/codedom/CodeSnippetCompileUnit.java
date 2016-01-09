package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeSnippetCompileUnit : CodeCompileUnit
public class CodeSnippetCompileUnit extends CodeCompileUnit implements Serializable
{
	private String value;

	private CodeLinePragma linePragma;

	public final String getValue()
	{
		if (this.value != null)
		{
			return this.value;
		}
		return "";
	}
	public final void setValue(String value)
	{
		this.value = value;
	}

	public final CodeLinePragma getLinePragma()
	{
		return this.getLinePragma();
	}
	public final void setLinePragma(CodeLinePragma value)
	{
		this.linePragma= value;
	}

	public CodeSnippetCompileUnit()
	{
	}

	public CodeSnippetCompileUnit(String value)
	{
		this.setValue(value);
	}
}