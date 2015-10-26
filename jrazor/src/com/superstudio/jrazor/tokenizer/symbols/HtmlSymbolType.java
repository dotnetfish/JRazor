package com.superstudio.jrazor.tokenizer.symbols;



public enum HtmlSymbolType
{
	Unknown(0),
	Text(1), // Text which isn't one of the below
	WhiteSpace(2), // Non-newline Whitespace
	NewLine(3), // Newline
	OpenAngle(4), // <
	Bang(5), // !
	Solidus(6), // /
	QuestionMark(7), // ?
	DoubleHyphen(8), // --
	LeftBracket(9), // [
	CloseAngle(10), // >
	RightBracket(11), // ]
	Equals(12), // =
	DoubleQuote(13), // "
	SingleQuote(14), // '
	Transition(15), // @
	Colon(16),
	RazorComment(17),
	RazorCommentStar(18),
	RazorCommentTransition(19);

	private int intValue;
	private static java.util.HashMap<Integer, HtmlSymbolType> mappings;
	private synchronized static java.util.HashMap<Integer, HtmlSymbolType> getMappings()
	{
		if (mappings == null)
		{
			mappings = new java.util.HashMap<Integer, HtmlSymbolType>();
		}
		return mappings;
	}

	private HtmlSymbolType(int value)
	{
		intValue = value;
		HtmlSymbolType.getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static HtmlSymbolType forValue(int value)
	{
		return getMappings().get(value);
	}
}