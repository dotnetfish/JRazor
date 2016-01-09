package com.superstudio.web.razor.text;

import com.superstudio.commons.TextReader;



public class SeekableTextReader extends TextReader implements ITextDocument
{
	private int _position = 0;
	private LineTrackingStringBuffer _buffer = new LineTrackingStringBuffer();
	private SourceLocation _location = SourceLocation.Zero;
	private Character _current;

	public SeekableTextReader(String content)
	{
		super(content);
		_buffer.append(content);
		UpdateState();
	}

	public SeekableTextReader(TextReader source)
	{
		this(source.readToEnd());
	}

	public SeekableTextReader(ITextBuffer buffer)
	{
		this(buffer.readToEnd());
	}

	public final SourceLocation getLocation()
	{
		return _location;
	}

	public final int getLength()
	{
		return _buffer.getLength();
	}

	public final int getPosition()
	{
		return _position;
	}
	public final void setPosition(int value)
	{
		if (_position != value)
		{
			_position = value;
			UpdateState();
		}
	}

	public final LineTrackingStringBuffer getBuffer()
	{
		return _buffer;
	}

	@Override
	public int read()
	{
		if (_current == null)
		{
			return -1;
		}
		char chr = _current;
		_position++;
		UpdateState();
		return chr;
	}

	@Override
	public int peek()
	{
		if (_current == null)
		{
			return -1;
		}
		return _current;
	}

	private void UpdateState()
	{
		if (_position < _buffer.getLength())
		{
			LineTrackingStringBuffer.CharacterReference chr = _buffer.charAt(_position);
			_current = chr.getCharacter();
			_location = chr.getLocation().clone();
		}
		else if (_buffer.getLength() == 0)
		{
			_current = null;
			_location = SourceLocation.Zero;
		}
		else
		{
			_current = null;
			_location = _buffer.getEndLocation().clone();
		}
	}
}