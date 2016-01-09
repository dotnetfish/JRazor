package com.superstudio.web.razor.text;

import com.superstudio.web.razor.parser.ParserHelpers;



public class LineTrackingStringBuffer
{
	private TextLine _currentLine;
	private TextLine _endLine;
	private java.util.List<TextLine> _lines;

	public LineTrackingStringBuffer()
	{
		_endLine = new TextLine(0, 0);
		_lines = new java.util.ArrayList<TextLine>(java.util.Arrays.asList(new TextLine[] { _endLine }));
	}

	public final int getLength()
	{
		return _endLine.getEnd();
	}

	public final SourceLocation getEndLocation()
	{
		return new SourceLocation(getLength(), _lines.size() - 1, _lines.get(_lines.size() - 1).getLength());
	}

	public final void append(String content)
	{
		for (int i = 0; i < content.length(); i++)
		{
			appendCore(content.charAt(i));

			// \r on it's own: start a new line, otherwise wait for \n
			// Other Newline: start a new line
			if ((content.charAt(i) == '\r' && (i + 1 == content.length() || content.charAt(i + 1) != '\n')) || (content.charAt(i) != '\r' && ParserHelpers.isNewLine(content.charAt(i))))
			{
				pushNewLine();
			}
		}
	}

	public final CharacterReference charAt(int absoluteIndex)
	{
		TextLine line = findLine(absoluteIndex);
		if (line == null)
		{
			//throw new ArgumentOutOfRangeException("absoluteIndex");
		}
		int idx = absoluteIndex - line.getStart();
		return new CharacterReference(line.getContent().charAt(idx), new SourceLocation(absoluteIndex, line.getIndex(), idx));
	}

	private void pushNewLine()
	{
		_endLine = new TextLine(_endLine.getEnd(), _endLine.getIndex() + 1);
		_lines.add(_endLine);
	}

	private void appendCore(char chr)
	{
		assert _lines.size() > 0;
		_lines.get(_lines.size() - 1).getContent().append(chr);
	}

	private TextLine findLine(int absoluteIndex)
	{
		TextLine selected = null;

		if (_currentLine != null)
		{
			if (_currentLine.contains(absoluteIndex))
			{
				// This index is on the last read line
				selected = _currentLine;
			}
			else if (absoluteIndex > _currentLine.getIndex() && _currentLine.getIndex() + 1 < _lines.size())
			{
				// This index is ahead of the last read line
				selected = scanLines(absoluteIndex, _currentLine.getIndex());
			}
		}

		// Have we found a line yet?
		if (selected == null)
		{
			// Scan from line 0
			selected = scanLines(absoluteIndex, 0);
		}

		assert selected == null || selected.contains(absoluteIndex);
		_currentLine = selected;
		return selected;
	}

	private TextLine scanLines(int absoluteIndex, int startPos)
	{
		for (int i = 0; i < _lines.size(); i++)
		{
			int idx = (i + startPos) % _lines.size();
			assert idx >= 0 && idx < _lines.size();

			if (_lines.get(idx).contains(absoluteIndex))
			{
				return _lines.get(idx);
			}
		}
		return null;
	}

	public static class CharacterReference
	{
		public CharacterReference(char character, SourceLocation location)
		{
			setCharacter(character);
			setLocation(location);
		}

		private char privateCharacter;
		public final char getCharacter()
		{
			return privateCharacter;
		}
		private void setCharacter(char value)
		{
			privateCharacter = value;
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
	}

	private static class TextLine
	{
		private StringBuilder _content = new StringBuilder();

		public TextLine(int start, int index)
		{
			setStart(start);
			setIndex(index);
		}

		public final StringBuilder getContent()
		{
			return _content;
		}

		public final int getLength()
		{
			return getContent().length();
		}

		private int privateStart;
		public final int getStart()
		{
			return privateStart;
		}
		public final void setStart(int value)
		{
			privateStart = value;
		}
		private int privateIndex;
		public final int getIndex()
		{
			return privateIndex;
		}
		public final void setIndex(int value)
		{
			privateIndex = value;
		}

		public final int getEnd()
		{
			return getStart() + getLength();
		}

		public final boolean contains(int index)
		{
			return index < getEnd() && index >= getStart();
		}
	}
}