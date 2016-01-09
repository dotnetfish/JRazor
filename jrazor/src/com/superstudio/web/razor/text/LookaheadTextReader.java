package com.superstudio.web.razor.text;


import com.superstudio.commons.TextReader;



public abstract class LookaheadTextReader extends TextReader
{
	public abstract SourceLocation getCurrentLocation();
	public abstract AutoCloseable beginLookahead();
	public abstract void cancelBacktrack();
}