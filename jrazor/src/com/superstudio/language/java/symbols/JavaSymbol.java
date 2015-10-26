package com.superstudio.language.java.symbols;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.jrazor.parser.syntaxTree.RazorError;
import com.superstudio.jrazor.text.SourceLocation;
import com.superstudio.jrazor.tokenizer.symbols.SymbolBase;

public class JavaSymbol extends SymbolBase<JavaSymbolType> {
	// Helper constructor
	public JavaSymbol(int offset, int line, int column, String content, JavaSymbolType type)
			throws ArgumentNullException {
		this(new SourceLocation(offset, line, column), content, type, null);
	}

	public JavaSymbol(SourceLocation start, String content, JavaSymbolType type) throws ArgumentNullException {
		this(start, content, type, null);
	}

	public JavaSymbol(int offset, int line, int column, String content, JavaSymbolType type,
			Iterable<RazorError> errors) throws ArgumentNullException {
		super(new SourceLocation(offset, line, column), content, type, errors);
	}

	public JavaSymbol(SourceLocation start, String content, JavaSymbolType type, Iterable<RazorError> errors)
			throws ArgumentNullException {
		super(start, content, type, errors);
	}

	private Boolean privateEscapedIdentifier;

	public final Boolean getEscapedIdentifier() {
		return privateEscapedIdentifier;
	}

	public final void setEscapedIdentifier(Boolean value) {
		privateEscapedIdentifier = value;
	}

	private JavaKeyword privateKeyword;

	public final JavaKeyword getKeyword() {
		return privateKeyword;
	}

	public final void setKeyword(JavaKeyword value) {
		privateKeyword = value;
	}

	@Override
	public boolean equals(Object obj) {
		JavaSymbol other = (JavaSymbol) ((obj instanceof JavaSymbol) ? obj : null);

		return super.equals(obj) && other.getKeyword().equals(getKeyword());
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ getKeyword().hashCode();
	}
}