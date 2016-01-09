package com.superstudio.web.razor.parser.syntaxTree;




//[Flags]
public enum AcceptedCharacters
{
	None(0),
	NewLine(1),
	WhiteSpace(2),


		NonWhiteSpace(4),

	AllWhiteSpace(1 | 2),
	Any(1 | 2 | 4),


	AnyExceptNewline(4 | 2);

	private int intValue;
	private static java.util.HashMap<Integer, AcceptedCharacters> mappings;
	private synchronized static java.util.HashMap<Integer, AcceptedCharacters> getMappings()
	{
		if (mappings == null)
		{
			mappings = new java.util.HashMap<Integer, AcceptedCharacters>();
		}
		return mappings;
	}

	AcceptedCharacters(int value)
	{
		intValue = value;
		AcceptedCharacters.getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static AcceptedCharacters forValue(int value)
	{
		return getMappings().get(value);
	}
}