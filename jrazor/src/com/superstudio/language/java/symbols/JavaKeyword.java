package com.superstudio.language.java.symbols;



public enum JavaKeyword
{
	Abstract,
	Byte,
	Class,	
	If,
	New,
	Override,
	Readonly,
	Short,
	Struct,
	Try,
	Final,
	Instanceof,
	Do,
	Switch,
	Ushort,
	While,
	Case,
	Const,
	Explicit,
	Float,
	Null,
	Implements,
	Extends,
	Implicit,
	Private,
	This,
	Import,
	Transient,
	Return,
	Volatile,//用在变量的声明中表示这个变量是被同时运行的几个线程异步修改的
	Native,
	Synchronized,
	Package,
	Long,
	Super,
	Catch,
	Continue,
	Double,
	For,
	In,
	Lock,
	Object,
	Protected,
	Static,
	False,
	Public,
	Sbyte,
	Throw,
	Throws,
	Decimal,
	Else,
	Operator,
	String,
	
	Bool,
	Char,
	Default,
	Foreach,
	
	Void,
	Enum,
	Finally,
	Int,
	Out,
	
	True,
	
	
	Interface,
	Break,
	Checked,
	Namespace;

	public int getValue()
	{
		return this.ordinal();
	}

	public static JavaKeyword forValue(int value)
	{
		return values()[value];
	}
}