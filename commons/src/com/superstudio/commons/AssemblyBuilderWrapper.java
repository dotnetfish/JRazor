package com.superstudio.commons;

import com.superstudio.codedom.CodeCompileUnit;
import com.superstudio.commons.compilation.BuildProvider;

public final class AssemblyBuilderWrapper implements IAssemblyBuilder
{
	private AssemblyBuilder InnerBuilder;
	public AssemblyBuilder getInnerBuilder()
	{
		return InnerBuilder;
	}
	public void setInnerBuilder(AssemblyBuilder value)
	{
		InnerBuilder = value;
	}
	public AssemblyBuilderWrapper(AssemblyBuilder builder)
	{
		if (builder == null)
		{
			throw new IllegalArgumentException("builder");
		}
		this.setInnerBuilder(builder);
	}
	
	@Override
	public void AddCodeCompileUnit(BuildProvider buildProvider, CodeCompileUnit compileUnit)
	{
		this.getInnerBuilder().AddCodeCompileUnit(buildProvider, compileUnit);
	}
	public void GenerateTypeFactory(String typeName)
	{
		this.getInnerBuilder().GenerateTypeFactory(typeName);
	}
	
	
	
}