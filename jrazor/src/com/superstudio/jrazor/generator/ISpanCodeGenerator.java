package com.superstudio.jrazor.generator;

import com.superstudio.jrazor.parser.syntaxTree.Span;




public interface ISpanCodeGenerator
{
	void generateCode(Span target, CodeGeneratorContext context);
}