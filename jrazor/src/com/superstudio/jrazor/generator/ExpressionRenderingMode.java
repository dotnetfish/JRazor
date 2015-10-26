package com.superstudio.jrazor.generator;



public enum ExpressionRenderingMode
{
	/** 
	 Indicates that expressions should be written to the output stream
	 
	 <example>
	 If @foo is rendered with WriteToOutput, the code generator would output the following code:
	 
	 Write(foo);
	 </example>
	*/
	WriteToOutput,

	/** 
	 Indicates that expressions should simply be placed as-is in the code, and the context in which
	 the code exists will be used to render it
	 
	 <example>
	 If @foo is rendered with InjectCode, the code generator would output the following code:
	 
	 foo
	 </example>
	*/
	InjectCode;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ExpressionRenderingMode forValue(int value)
	{
		return values()[value];
	}
}