package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeAttributeArgument
public class CodeAttributeArgument implements Serializable
{
	private String name;

	private CodeExpression value;

	public final String getName()
	{
		if (this.name != null)
		{
			return this.name;
		}
		return "";
	}
	public final void setName(String value)
	{
		this.name = value;
	}

	public final CodeExpression getValue()
	{
		return this.value;
	}
	public final void setValue(CodeExpression value)
	{
		this.value = value;
	}

	public CodeAttributeArgument()
	{
	}

	public CodeAttributeArgument(CodeExpression value)
	{
		this.setValue(value);
	}

	public CodeAttributeArgument(String name, CodeExpression value)
	{
		this.setName(name);
		this.setValue(value);
	}
}