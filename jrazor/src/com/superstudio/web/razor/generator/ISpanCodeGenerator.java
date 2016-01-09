package com.superstudio.web.razor.generator;

import com.superstudio.web.razor.parser.syntaxTree.Span;



public interface ISpanCodeGenerator
{
	void generateCode(Span target, CodeGeneratorContext context);
}