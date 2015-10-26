package com.superstudio.codedom.compiler;

public class LanguageOptions
{
	public static final LanguageOptions None = new LanguageOptions(0);
	public static final LanguageOptions CaseInsensitive = new LanguageOptions(1);

	private int intValue;
	private static java.util.HashMap<Integer, LanguageOptions> mappings;
	private static java.util.HashMap<Integer, LanguageOptions> getMappings()
	{
		if (mappings == null)
		{
			synchronized (LanguageOptions.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, LanguageOptions>();
				}
			}
		}
		return mappings;
	}

	private LanguageOptions(int value)
	{
		intValue = value;
		synchronized (LanguageOptions.class)
		{
			getMappings().put(value, this);
		}
	}

	public int getValue()
	{
		return intValue;
	}

	public static LanguageOptions forValue(int value)
	{
		synchronized (LanguageOptions.class)
		{
			LanguageOptions enumObj = getMappings().get(value);
			if (enumObj == null)
			{
				return new LanguageOptions(value);
			}
			else
			{
				return enumObj;
			}
		}
	}
}