package com.superstudio.jrazor.generator;


import com.superstudio.jrazor.parser.syntaxTree.*;

public abstract class HybridCodeGenerator implements ISpanCodeGenerator, IBlockCodeGenerator
{
	public void generateStartBlockCode(Block target, CodeGeneratorContext context)
	{
	}

	public void generateEndBlockCode(Block target, CodeGeneratorContext context)
	{
	}

	public void generateCode(Span target, CodeGeneratorContext context)
	{
	}
}