package com.superstudio.codedom;

import com.superstudio.commons.Guid;

import java.io.Serializable;


public class CodeChecksumPragma extends CodeDirective implements Serializable
{
	private String fileName;



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



	public final byte[] getChecksumData()
	{
		return this.checksumData;
	}


	public final void setChecksumData(byte[] value)
	{
		this.checksumData = value;
	}

	public CodeChecksumPragma()
	{
	}


	public CodeChecksumPragma(String fileName, Guid checksumAlgorithmId, byte[] checksumData)
	{
		this.fileName = fileName;
		this.checksumAlgorithmId = checksumAlgorithmId;
		this.checksumData = checksumData;
	}
}