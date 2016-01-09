package com.superstudio.jrazor.text;

// TextBuffer with Location tracking
public interface ITextDocument extends ITextBuffer
{
	SourceLocation getLocation();
}