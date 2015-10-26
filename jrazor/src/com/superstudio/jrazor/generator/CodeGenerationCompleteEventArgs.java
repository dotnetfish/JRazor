package com.superstudio.jrazor.generator;

import com.superstudio.codedom.CodeCompileUnit;
import com.superstudio.commons.EventArgs;
import com.superstudio.commons.csharpbridge.StringHelper;

public class CodeGenerationCompleteEventArgs implements EventArgs
{
	public CodeGenerationCompleteEventArgs(String virtualPath, String physicalPath, CodeCompileUnit generatedCode)
	{
		if (StringHelper.isNullOrEmpty(virtualPath))
		{
			//throw new IllegalArgumentException(CommonResources.getArgument_Cannot_Be_Null_Or_Empty(), "virtualPath");
		}
		if (generatedCode == null)
		{
			//throw new ArgumentNullException("generatedCode");
		}
		setVirtualPath(virtualPath);
		setPhysicalPath(physicalPath);
		setGeneratedCode(generatedCode);
	}

	private CodeCompileUnit privateGeneratedCode;
	public final CodeCompileUnit getGeneratedCode()
	{
		return privateGeneratedCode;
	}
	private void setGeneratedCode(CodeCompileUnit value)
	{
		privateGeneratedCode = value;
	}
	private String privateVirtualPath;
	public final String getVirtualPath()
	{
		return privateVirtualPath;
	}
	private void setVirtualPath(String value)
	{
		privateVirtualPath = value;
	}
	private String privatePhysicalPath;
	public final String getPhysicalPath()
	{
		return privatePhysicalPath;
	}
	private void setPhysicalPath(String value)
	{
		privatePhysicalPath = value;
	}
}