package com.superstudio.web.razor.editor;

import com.superstudio.web.razor.parser.syntaxTree.AcceptedCharacters;
import com.superstudio.web.razor.tokenizer.symbols.ISymbol;

import java.util.function.Function;

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