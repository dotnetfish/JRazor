package com.superstudio.jrazor.editor;

import java.util.function.Function;

import com.superstudio.jrazor.parser.syntaxTree.*;
import com.superstudio.jrazor.tokenizer.symbols.*;

public class SingleLineMarkupEditHandler extends SpanEditHandler
{
 
		public SingleLineMarkupEditHandler(Function<String, Iterable<? extends ISymbol>> tokenizer)
	{
		super(tokenizer);
	}

 
		public SingleLineMarkupEditHandler(Function<String, Iterable<? extends ISymbol>> tokenizer, AcceptedCharacters accepted)
	{
		super(tokenizer, accepted);
	}
}