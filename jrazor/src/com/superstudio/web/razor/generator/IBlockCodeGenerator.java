package com.superstudio.web.razor.generator;

import com.superstudio.web.razor.parser.syntaxTree.Block;



public interface IBlockCodeGenerator
{
	void generateStartBlockCode(Block target, CodeGeneratorContext context);
	void generateEndBlockCode(Block target, CodeGeneratorContext context);
}