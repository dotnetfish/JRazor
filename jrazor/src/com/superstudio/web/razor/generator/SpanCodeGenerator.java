package com.superstudio.web.razor.generator;

import com.superstudio.web.razor.parser.syntaxTree.Span;



public abstract class SpanCodeGenerator implements ISpanCodeGenerator
{

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