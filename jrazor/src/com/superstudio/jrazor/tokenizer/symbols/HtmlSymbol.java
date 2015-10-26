package com.superstudio.jrazor.tokenizer.symbols;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.jrazor.parser.syntaxTree.RazorError;
import com.superstudio.jrazor.text.SourceLocation;



public class HtmlSymbol extends SymbolBase<HtmlSymbolType> {
	// Helper constructor
	public HtmlSymbol(int offset, int line, int column, String content, HtmlSymbolType type) throws ArgumentNullException {
		this(new SourceLocation(offset, line, column), content, type, null);
	}

	public HtmlSymbol(SourceLocation start, String content, HtmlSymbolType type) throws ArgumentNullException {
		super(start, content, type, null);
	}

	public HtmlSymbol(int offset, int line, int column, String content, HtmlSymbolType type,
			Iterable<RazorError> errors) throws ArgumentNullException {
		super(new SourceLocation(offset, line, column), content, type, errors);
	}

	public HtmlSymbol(SourceLocation start, String content, HtmlSymbolType type, Iterable<RazorError> errors)
			throws ArgumentNullException {
		super(start, content, type, errors);
	}
}