package com.superstudio.web.razor.text;

import com.superstudio.web.razor.parser.ParserHelpers;



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

		updateInternalState();
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
			updateInternalState();
		}
	}

	public final void updateLocation(char characterRead, char nextCharacter)
	{
		updateCharacterCore(characterRead, nextCharacter);
		recalculateSourceLocation();
	}

	public final SourceLocationTracker updateLocation(String content)
	{
		for (int i = 0; i < content.length(); i++)
		{
			char nextCharacter = '\0';
			if (i < content.length() - 1)
			{
				nextCharacter = content.charAt(i + 1);
			}
			updateCharacterCore(content.charAt(i), nextCharacter);
		}
		recalculateSourceLocation();
		return this;
	}

	private void updateCharacterCore(char characterRead, char nextCharacter)
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

	private void updateInternalState()
	{
		_absoluteIndex = getCurrentLocation().getAbsoluteIndex();
		_characterIndex = getCurrentLocation().getCharacterIndex();
		_lineIndex = getCurrentLocation().getLineIndex();
	}

	private void recalculateSourceLocation()
	{
		_currentLocation = new SourceLocation(_absoluteIndex, _lineIndex, _characterIndex);
	}

	public static SourceLocation calculateNewLocation(SourceLocation lastPosition, String newContent)
	{
		return new SourceLocationTracker(lastPosition).updateLocation(newContent).getCurrentLocation();
	}
}