package com.superstudio.jrazor.generator;

import com.superstudio.jrazor.parser.syntaxTree.Block;




public interface IBlockCodeGenerator
{
	void generateStartBlockCode(Block target, CodeGeneratorContext context);
	void generateEndBlockCode(Block target, CodeGeneratorContext context);
}