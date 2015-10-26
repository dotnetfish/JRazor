package com.superstudio.jrazor.parser;

import com.superstudio.jrazor.parser.syntaxTree.Block;




public interface ISyntaxTreeRewriter
{
	Block rewrite(Block input) throws Exception;

	
}