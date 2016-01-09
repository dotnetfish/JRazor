package com.superstudio.codedom;


//ORIGINAL LINE: [ComVisible(true)][Serializable] public enum MemberAttributes
public class MemberAttributes
{
	public final static int Abstract=1;
	public final static int Final=2;
	public final static int Static=3;
	public final static int Override=4;
	public final static int Const=5;
	public final static int New=16;
	public final static int Overloaded=256;
	public final static int Assembly=4096;
	public final static int FamilyAndAssembly=8192;
	public final static int Family=12288;
	public final static int FamilyOrAssembly=16384;
	public final static int Private=20480;
	public final static int Public=24576;
	public final static int AccessMask=61440;
	public final static int ScopeMask=15;
	public final static int VTableMask=240;

	private int intValue;
	private static java.util.HashMap<Integer, MemberAttributes> mappings;
	private static java.util.HashMap<Integer, MemberAttributes> getMappings()
	{
		if (mappings == null)
		{
			synchronized (MemberAttributes.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, MemberAttributes>();
				}
			}
		}
		return mappings;
	}

	private MemberAttributes(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static MemberAttributes forValue(int value)
	{
		MemberAttributes result=getMappings().get(value);
		return result==null?new MemberAttributes(value):result;
	}
}