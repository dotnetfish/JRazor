package com.superstudio.jrazor.text;

import com.superstudio.jrazor.parser.syntaxTree.Span;



 
public final class TextChange {
	private String _newText;
	private String _oldText;

	public TextChange() {

	}

	/**
	 * Constructor for changes where the position hasn't moved (primarily for
	 * tests)
	 * 
	 */
	public TextChange(int position, int oldLength, ITextBuffer oldBuffer, int newLength, ITextBuffer newBuffer) {
		this(position, oldLength, oldBuffer, position, newLength, newBuffer);
	}

	public TextChange(int oldPosition, int oldLength, ITextBuffer oldBuffer, int newPosition, int newLength,
			ITextBuffer newBuffer) {
		this();
		/*
		 * if (oldPosition < 0) { throw new
		 * ArgumentOutOfRangeException("oldPosition", String.format(
		 * CommonResources.getArgument_Must_Be_GreaterThanOrEqualTo(), "0")); }
		 * if (newPosition < 0) { throw new
		 * ArgumentOutOfRangeException("newPosition", String.format(
		 * CommonResources.getArgument_Must_Be_GreaterThanOrEqualTo(), "0")); }
		 * if (oldLength < 0) { throw new
		 * ArgumentOutOfRangeException("oldLength", String.format(
		 * CommonResources.getArgument_Must_Be_GreaterThanOrEqualTo(), "0")); }
		 * if (newLength < 0) { throw new
		 * ArgumentOutOfRangeException("newLength", String.format(
		 * CommonResources.getArgument_Must_Be_GreaterThanOrEqualTo(), "0")); }
		 * if (oldBuffer == null) { //throw new
		 * ArgumentNullException("oldBuffer"); } if (newBuffer == null) {
		 * //throw new ArgumentNullException("newBuffer"); }
		 */

		setOldPosition(oldPosition);
		setNewPosition(newPosition);
		setOldLength(oldLength);
		setNewLength(newLength);
		setNewBuffer(newBuffer);
		setOldBuffer(oldBuffer);
	}

	private int privateOldPosition;

	public int getOldPosition() {
		return privateOldPosition;
	}

	private void setOldPosition(int value) {
		privateOldPosition = value;
	}

	private int privateNewPosition;

	public int getNewPosition() {
		return privateNewPosition;
	}

	private void setNewPosition(int value) {
		privateNewPosition = value;
	}

	private int privateOldLength;

	public int getOldLength() {
		return privateOldLength;
	}

	private void setOldLength(int value) {
		privateOldLength = value;
	}

	private int privateNewLength;

	public int getNewLength() {
		return privateNewLength;
	}

	private void setNewLength(int value) {
		privateNewLength = value;
	}

	private ITextBuffer privateNewBuffer;

	public ITextBuffer getNewBuffer() {
		return privateNewBuffer;
	}

	private void setNewBuffer(ITextBuffer value) {
		privateNewBuffer = value;
	}

	private ITextBuffer privateOldBuffer;

	public ITextBuffer getOldBuffer() {
		return privateOldBuffer;
	}

	private void setOldBuffer(ITextBuffer value) {
		privateOldBuffer = value;
	}

	/**
	 * <remark> Note: This property is not thread safe, and will move position
	 * on the textbuffer while being read.
	 * https://aspnetwebstack.codeplex.com/workitem/1317, tracks making this
	 * immutable and improving the access to ITextBuffer to be thread
	 * safe. </remark>
	 */
	public String getOldText() {
		if (_oldText == null && getOldBuffer() != null) {
			_oldText = getText(getOldBuffer(), getOldPosition(), getOldLength());
		}
		return _oldText;
	}

	/**
	 * <remark> Note: This property is not thread safe, and will move position
	 * on the textbuffer while being read.
	 * https://aspnetwebstack.codeplex.com/workitem/1317, tracks making this
	 * immutable and improving the access to ITextBuffer to be thread
	 * safe. </remark>
	 */
	public String getNewText() {
		if (_newText == null) {
			_newText = getText(getNewBuffer(), getNewPosition(), getNewLength());
		}
		return _newText;
	}

	public boolean getIsInsert() {
		return getOldLength() == 0 && getNewLength() > 0;
	}

	public boolean getIsDelete() {
		return getOldLength() > 0 && getNewLength() == 0;
	}

	public boolean getIsReplace() {
		return getOldLength() > 0 && getNewLength() > 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TextChange)) {
			return false;
		}
		TextChange change = (TextChange) obj;
		return (change.getOldPosition() == getOldPosition()) && (change.getNewPosition() == getNewPosition())
				&& (change.getOldLength() == getOldLength()) && (change.getNewLength() == getNewLength())
				&& getOldBuffer().equals(change.getOldBuffer()) && getNewBuffer().equals(change.getNewBuffer());
	}

	public String applyChange(String content, int changeOffset) {
		int changeRelativePosition = getOldPosition() - changeOffset;

		assert changeRelativePosition >= 0;
		return (content.substring(0, changeRelativePosition) + getNewText()
				+ content.substring(changeRelativePosition + getOldLength()));
	}

	/**
	 * Applies the text change to the content of the span and returns the new
	 * content. This method doesn't update the span content.
	 * 
	 */
	public String applyChange(Span span) {
		return applyChange(span.getContent(), span.getStart().getAbsoluteIndex());
	}

	@Override
	public int hashCode() {
		return getOldPosition() ^ getNewPosition() ^ getOldLength() ^ getNewLength() ^ getNewBuffer().hashCode()
				^ getOldBuffer().hashCode();
	}

	@Override
	public String toString() {
		return String.format("(%d:%d) \"%d\" -> (%d:%s) \"%s\"", 
				getOldPosition(), getOldLength(), getNewLength(),getOldPosition(),
				getOldText(), getNewText());
	}

	/**
	 * Removes a common prefix from the edit to turn IntelliSense replacements
	 * into insertions where possible
	 * 
	 * @return A normalized text change
	 */
	public TextChange normalize() {
		if (getOldBuffer() != null && getIsReplace() && getNewLength() > getOldLength()
				&& getNewText().startsWith(getOldText()) && getNewPosition() == getOldPosition()) {
			// Normalize the change into an insertion of the uncommon suffix
			// (i.e. strip out the common prefix)
			return new TextChange(getOldPosition() + getOldLength(), 0, getOldBuffer(),
					getOldPosition() + getOldLength(), getNewLength() - getOldLength(), getNewBuffer());
		}
		return this;
	}

	private static String getText(ITextBuffer buffer, int position, int length) {
		// Optimization for the common case of one char inserts, in this case we
		// don't even need to seek the buffer.
		if (length == 0) {
			return "";
		}

		int oldPosition = buffer.getPosition();
		try {
			buffer.setPosition(position);

			// Optimization for the common case of one char inserts, in this
			// case we seek the buffer.
			if (length == 1) {
				// char ch=new char(23);
				// Integer.
				return new Character(((char) buffer.read())).toString();
			} else {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < length; i++) {
					char c = (char) buffer.read();
					builder.append(c);

					// This check is probably not necessary, will revisit when
					// fixing https://aspnetwebstack.codeplex.com/workitem/1317
					if (Character.isHighSurrogate(c)) {
						builder.append((char) buffer.read());
					}
				}
				return builder.toString();
			}
		} finally {
			buffer.setPosition(oldPosition);
		}
	}

	public static boolean opEquality(TextChange left, TextChange right) {
		return left.equals(right);
	}

	public static boolean opInequality(TextChange left, TextChange right) {
		return !left.equals(right);
	}

	public TextChange clone() {
		TextChange varCopy = new TextChange();

		varCopy._newText = this._newText;
		varCopy._oldText = this._oldText;

		return varCopy;
	}
}