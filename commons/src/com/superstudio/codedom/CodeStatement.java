package com.superstudio.codedom;
import java.io.Serializable;
public class CodeStatement extends CodeObject implements Serializable
{
	private CodeLinePragma linePragma;

	private CodeDirectiveCollection startDirectives;

	private CodeDirectiveCollection endDirectives;

	public final CodeLinePragma getLinePragma()
	{
		return this.linePragma;
	}
	public final void setLinePragma(CodeLinePragma value)
	{
		this.linePragma = value;
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
			this.endDirectives = new CodeDirectiveCollection();
		}
		return this.endDirectives;
	}
}