package com.superstudio.web.razor.parser;



//[Flags]
public enum BalancingModes
{
	None(0),
	BacktrackOnFailure(1),
	NoErrorOnFailure(2),
	AllowCommentsAndTemplates(4),
	AllowEmbeddedTransitions(8);

	private int intValue;
	private static java.util.HashMap<Integer, BalancingModes> mappings;
	private synchronized static java.util.HashMap<Integer, BalancingModes> getMappings()
	{
		if (mappings == null)
		{
			mappings = new java.util.HashMap<Integer, BalancingModes>();
		}
		return mappings;
	}

	BalancingModes(int value)
	{
		intValue = value;
		BalancingModes.getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static BalancingModes forValue(int value)
	{
		BalancingModes result=getMappings().get(value);
		return result==null?BalancingModes.None:result;
	}

	public boolean HasFlag(BalancingModes noerroronfailure2) {
		// TODO Auto-generated method stub
		return (this.intValue & noerroronfailure2.getValue()) ==noerroronfailure2.getValue();
	}
}