package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeLinePragma
public class CodeLinePragma implements Serializable
{
	private String fileName;

	private int lineNumber;

	public final String getFileName()
	{
		if (this.fileName != null)
		{
			return this.fileName;
		}
		return "";
	}
	public final void setFileName(String value)
	{
		this.fileName = value;
	}

	public final int getLineNumber()
	{
		return this.lineNumber;
	}
	public final void setLineNumber(int value)
	{
		this.lineNumber = value;
	}

	public CodeLinePragma()
	{
	}

	public CodeLinePragma(String fileName, int lineNumber)
	{
		this.setFileName(fileName);
		this.setLineNumber(lineNumber);
	}
}