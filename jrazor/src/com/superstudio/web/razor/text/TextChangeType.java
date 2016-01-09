package com.superstudio.web.razor.text;


public enum TextChangeType
{
	Insert,
	Remove;

	public int getValue()
	{
		return this.ordinal();
	}

	public static TextChangeType forValue(int value)
	{
		return values()[value];
	}
}