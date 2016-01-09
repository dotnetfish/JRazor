package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeStatement : CodeObject
public class CodeStatement extends CodeObject implements Serializable
{
	private CodeLinePragma linePragma;


//ORIGINAL LINE: [OptionalField] private CodeDirectiveCollection startDirectives;
	private CodeDirectiveCollection startDirectives;


//ORIGINAL LINE: [OptionalField] private CodeDirectiveCollection endDirectives;
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