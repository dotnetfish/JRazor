package com.superstudio.codedom;

 
//ORIGINAL LINE: [ComVisible(true)][Serializable] public enum CodeRegionMode
public enum CodeRegionMode
{
	None,
	Start,
	End;

	public int getValue()
	{
		return this.ordinal();
	}

	public static CodeRegionMode forValue(int value)
	{
		return values()[value];
	}
}