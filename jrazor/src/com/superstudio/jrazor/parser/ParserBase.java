package com.superstudio.jrazor.parser;

import com.superstudio.commons.NotSupportedException;
import com.superstudio.commons.Tuple;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.jrazor.parser.syntaxTree.SpanBuilder;
import com.superstudio.jrazor.resources.RazorResources;
import com.superstudio.jrazor.text.SourceLocation;




public abstract class ParserBase
{
	private ParserContext _context;

	public ParserContext getContext()
	{
		return _context;
	}
	public void setContext(ParserContext value)
	{
		//Debug.Assert(_context == null, "Context has already been set for this parser!");
		_context = value;
		_context.assertOnOwnerTask();
	}

	public boolean getIsMarkupParser()
	{
		return false;
	}

	protected abstract ParserBase getOtherParser();

	public abstract void buildSpan(SpanBuilder span, SourceLocation start, String content);

	public abstract void parseBlock() throws Exception ;

	// Markup Parsers need the ParseDocument and ParseSection methods since the markup parser is the first parser to hit the document 
	// and the logic may be different than the ParseBlock method.
	public void parseDocument() throws Exception
	{
		assert getIsMarkupParser();
		throw new NotSupportedException(RazorResources.getParserIsNotAMarkupParser());
	}

	public void parseSection(Tuple<String, String> nestingSequences, boolean caseSensitive) throws Exception
	{
		assert getIsMarkupParser();
		throw new NotSupportedException(RazorResources.getParserIsNotAMarkupParser());
	}
}