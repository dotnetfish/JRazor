package com.superstudio.commons;

import com.superstudio.codedom.CodeCompileUnit;
import com.superstudio.commons.compilation.BuildProvider;

public interface IAssemblyBuilder
{
	void addCodeCompileUnit(BuildProvider buildProvider, CodeCompileUnit compileUnit);
	void generateTypeFactory(String typeName);
}