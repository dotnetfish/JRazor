package com.superstudio.codedom;

import java.io.Serializable;

import com.superstudio.commons.Guid;

 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeChecksumPragma : CodeDirective
public class CodeChecksumPragma extends CodeDirective implements Serializable
{
	private String fileName;

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: private byte[] checksumData;
	private byte[] checksumData;

	private Guid checksumAlgorithmId = new Guid();

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

	public final Guid getChecksumAlgorithmId()
	{
		return this.checksumAlgorithmId;
	}
	public final void setChecksumAlgorithmId(Guid value)
	{
		this.checksumAlgorithmId = value;
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public byte[] getChecksumData()
	public final byte[] getChecksumData()
	{
		return this.checksumData;
	}
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public void setChecksumData(byte[] value)
	public final void setChecksumData(byte[] value)
	{
		this.checksumData = value;
	}

	public CodeChecksumPragma()
	{
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public CodeChecksumPragma(string fileName, Guid checksumAlgorithmId, byte[] checksumData)
	public CodeChecksumPragma(String fileName, Guid checksumAlgorithmId, byte[] checksumData)
	{
		this.fileName = fileName;
		this.checksumAlgorithmId = checksumAlgorithmId;
		this.checksumData = checksumData;
	}
}