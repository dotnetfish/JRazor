package com.superstudio.codedom;


//ORIGINAL LINE: [ComVisible(true)][Serializable] public enum FieldDirection
public enum FieldDirection
{
	In,
	Out,
	Ref;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FieldDirection forValue(int value)
	{
		return values()[value];
	}
}