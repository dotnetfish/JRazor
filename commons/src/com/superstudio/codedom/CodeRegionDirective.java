package com.superstudio.codedom;
import java.io.Serializable;
 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeRegionDirective : CodeDirective
public class CodeRegionDirective extends CodeDirective implements Serializable
{
	private String regionText;

	private CodeRegionMode regionMode = CodeRegionMode.values()[0];

	public final String getRegionText()
	{
		if (this.regionText != null)
		{
			return this.regionText;
		}
		return "";
	}
	public final void setRegionText(String value)
	{
		this.regionText = value;
	}

	public final CodeRegionMode getRegionMode()
	{
		return this.regionMode;
	}
	public final void setRegionMode(CodeRegionMode value)
	{
		this.regionMode = value;
	}

	public CodeRegionDirective()
	{
	}

	public CodeRegionDirective(CodeRegionMode regionMode, String regionText)
	{
		this.setRegionText(regionText);
		this.regionMode = regionMode;
	}
}