package com.superstudio.jrazor.generator;

import com.superstudio.jrazor.parser.syntaxTree.Span;




public abstract class SpanCodeGenerator implements ISpanCodeGenerator
{
 
	//[SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "This class has no instance state")]
	public static final ISpanCodeGenerator Null = new NullSpanCodeGenerator();

	public void generateCode(Span target, CodeGeneratorContext context)
	{
	}

	@Override
	public boolean equals(Object obj)
	{
		return ((obj instanceof ISpanCodeGenerator) ? obj : null) != null;
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	private static class NullSpanCodeGenerator implements ISpanCodeGenerator
	{
		public final void generateCode(Span target, CodeGeneratorContext context)
		{
		}

		@Override
		public String toString()
		{
			return "None";
		}
	}
}