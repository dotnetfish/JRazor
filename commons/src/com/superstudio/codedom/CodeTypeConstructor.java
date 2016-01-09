package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeTypeConstructor : CodeMemberMethod
public class CodeTypeConstructor extends CodeMemberMethod implements Serializable
{
	public CodeTypeConstructor()
	{
		//super.getName() = ".cctor";
		setName(".cctor");
	}
}