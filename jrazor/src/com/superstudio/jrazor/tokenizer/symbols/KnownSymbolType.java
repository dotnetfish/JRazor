package com.superstudio.jrazor.tokenizer.symbols;



public enum KnownSymbolType
{
	WhiteSpace,
	NewLine,
	Identifier,
	Keyword,
	Transition,
	Unknown,
	CommentStart,
	CommentStar,
	CommentBody;

	public int getValue()
	{
		return this.ordinal();
	}

	public static KnownSymbolType forValue(int value)
	{
		return values()[value];
	}
}