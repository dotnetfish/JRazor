package com.superstudio.web.razor.generator;

import com.superstudio.web.razor.parser.syntaxTree.Block;
import com.superstudio.web.razor.parser.syntaxTree.Span;

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