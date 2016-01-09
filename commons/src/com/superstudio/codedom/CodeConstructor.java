package com.superstudio.codedom;
import java.io.Serializable;
public class CodeConstructor extends CodeMemberMethod implements Serializable
{
	private CodeExpressionCollection baseConstructorArgs =   new CodeExpressionCollection();

	private CodeExpressionCollection chainedConstructorArgs =   new CodeExpressionCollection();

	public final CodeExpressionCollection getBaseConstructorArgs()
	{
		return this.baseConstructorArgs;
	}

	public final CodeExpressionCollection getChainedConstructorArgs()
	{
		return this.chainedConstructorArgs;
	}

	public CodeConstructor()
	{
		//super.getName() = ".ctor";
		setName(".ctor");
	}
}