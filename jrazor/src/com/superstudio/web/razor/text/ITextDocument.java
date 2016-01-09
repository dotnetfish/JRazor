package com.superstudio.web.razor.text;

// TextBuffer with Location tracking
public interface ITextDocument extends ITextBuffer
{
	SourceLocation getLocation();
}