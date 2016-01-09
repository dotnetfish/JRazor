package com.superstudio.web.razor.generator;


import com.superstudio.commons.IEquatable;
import com.superstudio.web.razor.parser.syntaxTree.Block;
public abstract class BlockCodeGenerator implements IBlockCodeGenerator,IEquatable<Object>
{
	public static final IBlockCodeGenerator Null = new NullBlockCodeGenerator();

	public void generateStartBlockCode(Block target, CodeGeneratorContext context)
	{
	}

	public void generateEndBlockCode(Block target, CodeGeneratorContext context)
	{
	}

	@Override
	public boolean equals(Object obj)
	{
		return ((obj instanceof IBlockCodeGenerator) ? obj : null) != null;
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	private static class NullBlockCodeGenerator implements IBlockCodeGenerator
	{
		public final void generateStartBlockCode(Block target, CodeGeneratorContext context)
		{
		}

		public final void generateEndBlockCode(Block target, CodeGeneratorContext context)
		{
		}

		@Override
		public String toString()
		{
			return "None";
		}
	}
}