package com.superstudio.jrazor.editor;

import java.util.function.Function;

import com.superstudio.jrazor.parser.syntaxTree.*;
import com.superstudio.jrazor.tokenizer.symbols.*;

public class SingleLineMarkupEditHandler extends SpanEditHandler
{
 
	//[SuppressMessage("Microsoft.Design", "CA1006:DoNotNestGenericTypesInMemberSignatures", Justification = "Func<T> is the recommended delegate type and requires this level of nesting.")]
	public SingleLineMarkupEditHandler(Function<String, Iterable<? extends ISymbol>> tokenizer)
	{
		super(tokenizer);
	}

 
	//[SuppressMessage("Microsoft.Design", "CA1006:DoNotNestGenericTypesInMemberSignatures", Justification = "Func<T> is the recommended delegate type and requires this level of nesting.")]
	public SingleLineMarkupEditHandler(Function<String, Iterable<? extends ISymbol>> tokenizer, AcceptedCharacters accepted)
	{
		super(tokenizer, accepted);
	}
}