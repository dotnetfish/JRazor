package com.superstudio.commons;

import com.superstudio.codedom.CodeCompileUnit;
import com.superstudio.commons.compilation.BuildProvider;

public interface IAssemblyBuilder
{
	void AddCodeCompileUnit(BuildProvider buildProvider, CodeCompileUnit compileUnit);
	void GenerateTypeFactory(String typeName);
}