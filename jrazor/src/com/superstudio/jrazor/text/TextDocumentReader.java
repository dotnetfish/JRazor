package com.superstudio.jrazor.text;

import com.superstudio.commons.TextReader;




public class TextDocumentReader extends TextReader implements ITextDocument
{
	public TextDocumentReader(ITextDocument source)
	{
		setDocument(source);
	}

	private ITextDocument privateDocument;
	public final ITextDocument getDocument()
	{
		return privateDocument;
	}
	public final void setDocument(ITextDocument value)
	{
		privateDocument = value;
	}

	public final SourceLocation getLocation()
	{
		return getDocument().getLocation();
	}

	public final int getLength()
	{
		return getDocument().getLength();
	}

	public final int getPosition()
	{
		return getDocument().getPosition();
	}
	public final void setPosition(int value)
	{
		getDocument().setPosition(value);
	}

	@Override
	public int read()
	{
		return getDocument().read();
	}

	@Override
	public int peek()
	{
		return getDocument().peek();
	}
}