package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeLabeledStatement : CodeStatement
public class CodeLabeledStatement extends CodeStatement implements Serializable
{
	private String label;

	private CodeStatement statement;

	public final String getLabel()
	{
		if (this.label != null)
		{
			return this.label;
		}
		return "";
	}
	public final void setLabel(String value)
	{
		this.label = value;
	}

	public final CodeStatement getStatement()
	{
		return this.statement;
	}
	public final void setStatement(CodeStatement value)
	{
		this.statement = value;
	}

	public CodeLabeledStatement()
	{
	}

	public CodeLabeledStatement(String label)
	{
		this.label = label;
	}

	public CodeLabeledStatement(String label, CodeStatement statement)
	{
		this.label = label;
		this.statement = statement;
	}
}