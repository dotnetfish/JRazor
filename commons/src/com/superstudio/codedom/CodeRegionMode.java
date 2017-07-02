package com.superstudio.codedom;

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