package com.superstudio.jrazor.parser.syntaxTree;



public enum SpanKind
{
	Transition,
	MetaCode,
	Comment,
	Code,
	Markup;

	public int getValue()
	{
		return this.ordinal();
	}

	public static SpanKind forValue(int value)
	{
		return values()[value];
	}
}