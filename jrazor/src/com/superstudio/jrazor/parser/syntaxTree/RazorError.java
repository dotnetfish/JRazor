package com.superstudio.jrazor.parser.syntaxTree;

import com.superstudio.commons.IEquatable;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.text.SourceLocation;




public class RazorError implements IEquatable<RazorError>
{
	public RazorError(String message, SourceLocation location)
	{
		this(message, location, 1);
	}

	public RazorError(String message, int absoluteIndex, int lineIndex, int columnIndex)
	{
		this(message, new SourceLocation(absoluteIndex, lineIndex, columnIndex));
	}

	public RazorError(String message, SourceLocation location, int length)
	{
		setMessage(message);
		setLocation(location);
		setLength(length);
	}

	public RazorError(String message, int absoluteIndex, int lineIndex, int columnIndex, int length)
	{
		this(message, new SourceLocation(absoluteIndex, lineIndex, columnIndex), length);
	}

	private String privateMessage;
	public final String getMessage()
	{
		return privateMessage;
	}
	private void setMessage(String value)
	{
		privateMessage = value;
	}
	private SourceLocation privateLocation;
	public final SourceLocation getLocation()
	{
		return privateLocation;
	}
	private void setLocation(SourceLocation value)
	{
		privateLocation = value;
	}
	private int privateLength;
	public final int getLength()
	{
		return privateLength;
	}
	private void setLength(int value)
	{
		privateLength = value;
	}

	@Override
	public String toString()
	{
		return String.format( "Error @ %s(%s) - [%d]", getLocation().clone(), getMessage(), getLength());
	}

	@Override
	public boolean equals(Object obj)
	{
		RazorError err = (RazorError)((obj instanceof RazorError) ? obj : null);
		return (err != null) && (equals(err));
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	public final boolean equals(RazorError other)
	{
		return StringHelper.stringsEqual(other.getMessage(), getMessage()) && getLocation().equals(other.getLocation().clone());
	}

	@Override
	public boolean equals(RazorError obj, RazorError others) {
		// TODO Auto-generated method stub
		return obj.equals(others);
	}
}