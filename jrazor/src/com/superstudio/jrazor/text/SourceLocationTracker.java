package com.superstudio.jrazor.text;

import com.superstudio.jrazor.parser.ParserHelpers;




public class SourceLocationTracker
{
	private int _absoluteIndex = 0;
	private int _characterIndex = 0;
	private int _lineIndex = 0;
	private SourceLocation _currentLocation;

	public SourceLocationTracker()
	{
		this(SourceLocation.Zero);
	}

	public SourceLocationTracker(SourceLocation currentLocation)
	{
		setCurrentLocation(currentLocation);

		UpdateInternalState();
	}

	public final SourceLocation getCurrentLocation()
	{
		return _currentLocation;
	}
	public final void setCurrentLocation(SourceLocation value)
	{
		if (_currentLocation != value)
		{
			_currentLocation = value;
			UpdateInternalState();
		}
	}

	public final void UpdateLocation(char characterRead, char nextCharacter)
	{
		UpdateCharacterCore(characterRead, nextCharacter);
		RecalculateSourceLocation();
	}

	public final SourceLocationTracker UpdateLocation(String content)
	{
		for (int i = 0; i < content.length(); i++)
		{
			char nextCharacter = '\0';
			if (i < content.length() - 1)
			{
				nextCharacter = content.charAt(i + 1);
			}
			UpdateCharacterCore(content.charAt(i), nextCharacter);
		}
		RecalculateSourceLocation();
		return this;
	}

	private void UpdateCharacterCore(char characterRead, char nextCharacter)
	{
		_absoluteIndex++;

		if (ParserHelpers.isNewLine(characterRead) && (characterRead != '\r' || nextCharacter != '\n'))
		{
			_lineIndex++;
			_characterIndex = 0;
		}
		else
		{
			_characterIndex++;
		}
	}

	private void UpdateInternalState()
	{
		_absoluteIndex = getCurrentLocation().getAbsoluteIndex();
		_characterIndex = getCurrentLocation().getCharacterIndex();
		_lineIndex = getCurrentLocation().getLineIndex();
	}

	private void RecalculateSourceLocation()
	{
		_currentLocation = new SourceLocation(_absoluteIndex, _lineIndex, _characterIndex);
	}

	public static SourceLocation CalculateNewLocation(SourceLocation lastPosition, String newContent)
	{
		return new SourceLocationTracker(lastPosition).UpdateLocation(newContent).getCurrentLocation();
	}
}