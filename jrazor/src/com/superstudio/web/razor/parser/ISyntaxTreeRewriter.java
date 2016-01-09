package com.superstudio.web.razor.parser;

import com.superstudio.web.razor.parser.syntaxTree.Block;



public interface ISyntaxTreeRewriter
{
	Block rewrite(Block input) throws Exception;

	
}