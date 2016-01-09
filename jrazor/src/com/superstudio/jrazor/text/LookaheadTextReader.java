package com.superstudio.jrazor.text;

import com.superstudio.commons.TextReader;




public abstract class LookaheadTextReader extends TextReader
{
	public abstract SourceLocation getCurrentLocation();
	public abstract IDisposable beginLookahead();
	public abstract void cancelBacktrack();
}