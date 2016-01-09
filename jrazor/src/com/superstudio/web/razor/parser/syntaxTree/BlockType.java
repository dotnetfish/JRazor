package com.superstudio.web.razor.parser.syntaxTree;


public enum BlockType
{
	// Code
	Statement,
	Directive,
	Functions,
	Expression,
	Helper,

	// Markup
	Markup,
	Section,
	Template,

	// Special
	Comment;

	public int getValue()
	{
		return this.ordinal();
	}

	public static BlockType forValue(int value)
	{
		return values()[value];
	}
}