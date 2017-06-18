package com.superstudio.web.razor.parser;

import com.superstudio.commons.NotSupportedException;
import com.superstudio.commons.Tuple;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.web.RazorResources;
import com.superstudio.web.razor.parser.syntaxTree.SpanBuilder;
import com.superstudio.web.razor.text.SourceLocation;

import java.util.Locale;


public abstract class ParserBase
{
	private ParserContext _context;

	public ParserContext getContext()
	{
		return _context;
	}
	public void setContext(ParserContext value)
	{
		_context = value;
		_context.AssertOnOwnerTask();
	}

	public boolean getIsMarkupParser()
	{
		return false;
	}

	protected abstract ParserBase getOtherParser();

	public abstract void buildSpan(SpanBuilder span, SourceLocation start, String content);

	public abstract void parseBlock() ;

	// Markup Parsers need the ParseDocument and parseSection methods since the markup parser is the first parser to hit the document
	// and the logic may be different than the parseBlock method.
	public void ParseDocument() throws NotSupportedException, InvalidOperationException
	{
		assert getIsMarkupParser();
		throw new NotSupportedException(RazorResources.getResource(RazorResources.ParserIsNotAMarkupParser));
	}

	public void parseSection(Tuple<String, String> nestingSequences, boolean caseSensitive) throws NotSupportedException
	{
		assert getIsMarkupParser();
		throw new NotSupportedException(RazorResources.getResource(RazorResources.ParserIsNotAMarkupParser));
	}
}