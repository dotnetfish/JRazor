﻿package com.superstudio.language.java.symbols;



public enum JavaSymbolType
{
	Unknown,
	Identifier,
	Keyword,
	IntegerLiteral,
	NewLine,
	WhiteSpace,
	Comment,
	RealLiteral,
	CharacterLiteral,
	StringLiteral,

	// Operators
	Arrow,
	Minus,
	Decrement,
	MinusAssign,
	NotEqual,
	Not,
	Modulo,
	ModuloAssign,
	AndAssign,
	And,
	DoubleAnd,
	LeftParenthesis,
	RightParenthesis,
	Star,
	MultiplyAssign,
	Comma,
	Dot,
	Slash,
	DivideAssign,
	DoubleColon,
	Colon,
	Semicolon,
	QuestionMark,
	NullCoalesce,
	RightBracket,
	LeftBracket,
	XorAssign,
	Xor,
	LeftBrace,
	OrAssign,
	DoubleOr,
	Or,
	RightBrace,
	Tilde,
	Plus,
	PlusAssign,
	Increment,
	LessThan,
	LessThanEqual,
	LeftShift,
	LeftShiftAssign,
	Assign,
	Equals,
	GreaterThan,
	GreaterThanEqual,
	RightShift,
	RightShiftAssign,
	Hash,
	Transition,

	// Razor specific
	RazorCommentTransition,
	RazorCommentStar,
	RazorComment;

	public int getValue()
	{
		return this.ordinal();
	}

	public static JavaSymbolType forValue(int value)
	{
		return values()[value];
	}
}