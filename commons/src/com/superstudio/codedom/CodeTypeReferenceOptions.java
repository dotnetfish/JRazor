package com.superstudio.codedom;


//ORIGINAL LINE: [Flags, ComVisible(true)][Serializable] public enum CodeTypeReferenceOptions
public enum CodeTypeReferenceOptions
{
	
	 GlobalReference(1),//= new CodeTypeReferenceOptions(1);
	 GenericTypeParameter(2);// = new CodeTypeReferenceOptions(2);

	private int intValue;
	private static java.util.HashMap<Integer, CodeTypeReferenceOptions> mappings;
	private static java.util.HashMap<Integer, CodeTypeReferenceOptions> getMappings()
	{
		if (mappings == null)
		{
			synchronized (CodeTypeReferenceOptions.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, CodeTypeReferenceOptions>();
				}
			}
		}
		return mappings;
	}

	CodeTypeReferenceOptions(int value)
	{
		intValue = value;
		synchronized (CodeTypeReferenceOptions.class)
		{
			getMappings().put(value, this);
		}
	}

	public int getValue()
	{
		return intValue;
	}

	public static CodeTypeReferenceOptions forValue(int value)
	{
		synchronized (CodeTypeReferenceOptions.class)
		{
			CodeTypeReferenceOptions enumObj = getMappings().get(value);
			
				return enumObj;
			//new CodeTypeReferenceOptions(value);
		}
	}
}