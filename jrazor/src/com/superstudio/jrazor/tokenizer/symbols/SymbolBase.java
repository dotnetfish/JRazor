package com.superstudio.jrazor.tokenizer.symbols;

import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.jrazor.parser.syntaxTree.RazorError;
import com.superstudio.jrazor.text.SourceLocation;



public abstract class SymbolBase<TType> implements ISymbol {
	protected SymbolBase(SourceLocation start, String content, TType type, Iterable<RazorError> errors)
			throws ArgumentNullException {
		if (content == null) {
			//throw new ArgumentNullException("content");
		}
		if (type == null) {
			//throw new ArgumentNullException("type");
		}

		setStart(start);
		setContent(content);
		setType(type);
		setErrors(errors);
	}

	private SourceLocation privateStart;

	public final SourceLocation getStart() {
		return privateStart;
	}

	private void setStart(SourceLocation value) {
		privateStart = value;
	}

	private String privateContent;

	public final String getContent() {
		return privateContent;
	}

	private void setContent(String value) {
		privateContent = value;
	}

	private Iterable<RazorError> privateErrors;

	public final Iterable<RazorError> getErrors() {
		return privateErrors;
	}

	private void setErrors(Iterable<RazorError> value) {
		privateErrors = value;
	}

	 
	 
	// [SuppressMessage("Microsoft.Naming",
	// "CA1721:PropertyNamesShouldNotMatchGetMethods", Justification = "This is
	// the most appropriate name for this property and conflicts are unlikely")]
	private TType privateType;

	public final TType getType() {
		return privateType;
	}

	private void setType(TType value) {
		privateType = value;
	}

	@Override
	public boolean equals(Object obj) {
		@SuppressWarnings("unchecked")
		SymbolBase<TType> other = (SymbolBase<TType>) ((obj instanceof SymbolBase<?>) ? obj : null);
		return other != null && getStart().equals(other.getStart().clone())
				//&& String.equals(getContent(), other.getContent(), StringComparison.Ordinal)
				&& getContent().equalsIgnoreCase(other.getContent())
				&& getType().equals(other.getType());
	}

	@Override
	public int hashCode() {
		return HashCodeCombiner.Start().Add(getStart().clone()).Add(getContent()).Add(getType()).getCombinedHash();
	}

	@Override
	public String toString() {
		return String.format("%s %s - [%s]", getStart().clone(), getType(),
				getContent());
	}

	public final void offsetStart(SourceLocation documentStart) {
		setStart(SourceLocation.OpAddition(documentStart, getStart()));
	}

	public final void changeStart(SourceLocation newStart) {
		setStart(newStart);
	}
}