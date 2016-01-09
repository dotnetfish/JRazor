package com.superstudio.codedom;

public enum CodeBinaryOperatorType
{
	Add,
	Subtract,
	Multiply,
	Divide,
	Modulus,
	Assign,
	IdentityInequality,
	IdentityEquality,
	ValueEquality,
	BitwiseOr,
	BitwiseAnd,
	BooleanOr,
	BooleanAnd,
	LessThan,
	LessThanOrEqual,
	GreaterThan,
	GreaterThanOrEqual;

	public int getValue()
	{
		return this.ordinal();
	}

	public static CodeBinaryOperatorType forValue(int value)
	{
		return values()[value];
	}
}