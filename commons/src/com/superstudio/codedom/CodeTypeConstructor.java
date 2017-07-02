package com.superstudio.codedom;
import java.io.Serializable;
public class CodeTypeConstructor extends CodeMemberMethod implements Serializable
{
	public CodeTypeConstructor()
	{
		//super.getName() = ".cctor";
		setName(".cctor");
	}
}