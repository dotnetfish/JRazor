﻿package com.superstudio.jrazor.text;

import java.io.IOException;

import com.superstudio.commons.IDisposable;
import com.superstudio.commons.TextReader;
import com.superstudio.jrazor.utils.DisposableAction;




public class BufferingTextReader extends LookaheadTextReader
{
	private java.util.Stack<BacktrackContext> _backtrackStack = new java.util.Stack<BacktrackContext>();
	private int _currentBufferPosition;

	private int _currentCharacter;
	private SourceLocationTracker _locationTracker;

	public BufferingTextReader(TextReader source)
	{
		if (source == null)
		{
			//throw new ArgumentNullException("source");
		}

		setInnerReader(source);
		_locationTracker = new SourceLocationTracker();

		updateCurrentCharacter();
	}

	private StringBuilder privateBuffer;
	public final StringBuilder getBuffer()
	{
		return privateBuffer;
	}
	public final void setBuffer(StringBuilder value)
	{
		privateBuffer = value;
	}
	private boolean privateBuffering;
	public final boolean getBuffering()
	{
		return privateBuffering;
	}
	public final void setBuffering(boolean value)
	{
		privateBuffering = value;
	}
	private TextReader privateInnerReader;
	public final TextReader getInnerReader()
	{
		return privateInnerReader;
	}
	public final void setInnerReader(TextReader value)
	{
		privateInnerReader = value;
	}

	@Override
	public SourceLocation getCurrentLocation()
	{
		return _locationTracker.getCurrentLocation();
	}

	protected int getCurrentCharacter()
	{
		return _currentCharacter;
	}

	@Override
	public int read()
	{
		int ch = getCurrentCharacter();
		try {
			nextCharacter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ch;
	}

	// TODO: Optimize Read(char[],int,int) to copy direct from the buffer where possible

	@Override
	public int peek()
	{
		return getCurrentCharacter();
	}


	protected void dispose(boolean disposing)
	{
		if (disposing)
		{
			//getInnerReader().dispose();
		}
		//super.dispose(disposing);
	}

	@Override
	public IDisposable beginLookahead()
	{
		// Is this our first lookahead?
		if (getBuffer() == null)
		{
			// Yes, setup the backtrack buffer
			setBuffer(new StringBuilder());
		}

		if (!getBuffering())
		{
			// We're not already buffering, so we need to expand the buffer to hold the first character
			try {
				expandBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setBuffering(true);
		}

		// Mark the position to return to when we backtrack
		// Use the closures and the "using" statement rather than an explicit stack
		BacktrackContext tempVar = new BacktrackContext();
		tempVar.setBufferIndex(_currentBufferPosition);
		tempVar.setLocation(getCurrentLocation());
		BacktrackContext context = tempVar;
		_backtrackStack.push(context);
 
		return new DisposableAction(() ->
		{
			endLookahead(context);
		}
	   );
	}

	// REVIEW: This really doesn't sound like the best name for this...
	@Override
	public void cancelBacktrack()
	{
		if (_backtrackStack.empty())
		{
			//throw new InvalidOperationException(RazorResources.getCancelBacktrack_Must_Be_Called_Within_Lookahead());
		}
		// Just pop the current backtrack context so that when the lookahead ends, it won't be backtracked
		_backtrackStack.pop();
	}

	private void endLookahead(BacktrackContext context)
	{
		// If the specified context is not the one on the stack, it was popped by a call to DoNotBacktrack
		//if (_backtrackStack.size() > 0 && ReferenceEquals(_backtrackStack.peek(), context))
		if (_backtrackStack.size() > 0 && _backtrackStack.peek().equals(context))
		{
			_backtrackStack.pop();
			_currentBufferPosition = context.getBufferIndex();
			_locationTracker.setCurrentLocation(context.getLocation().clone());

			updateCurrentCharacter();
		}
	}

	protected void nextCharacter() throws IOException
	{
		int prevChar = getCurrentCharacter();
		if (prevChar == -1)
		{
			return; // We're at the end of the source
		}

		if (getBuffering())
		{
			if (_currentBufferPosition >= getBuffer().length() - 1)
			{
				// If there are no more lookaheads (thus no need to continue with the buffer) we can just clean up the buffer
				if (_backtrackStack.empty())
				{
					// Reset the buffer
					getBuffer().setLength(0);
					_currentBufferPosition = 0;
					setBuffering(false);
				}
				else if (!expandBuffer())
				{
					// Failed to expand the buffer, because we're at the end of the source
					_currentBufferPosition = getBuffer().length(); // Force the position past the end of the buffer
				}
			}
			else
			{
				// Not at the end yet, just advance the buffer pointer
				_currentBufferPosition++;
			}
		}
		else
		{
			// Just act like normal
			try {
				getInnerReader().read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Don't care about the return value, Peek() is used to get characters from the source
		}

		updateCurrentCharacter();
		_locationTracker.UpdateLocation((char)prevChar, (char)getCurrentCharacter());
	}

	protected final boolean expandBuffer() throws IOException
	{
		// Pull another character into the buffer and update the position
		int ch = getInnerReader().read();

		// Only append the character to the buffer if there actually is one
		if (ch != -1)
		{
			getBuffer().append((char)ch);
			_currentBufferPosition = getBuffer().length() - 1;
			return true;
		}
		return false;
	}

	private void updateCurrentCharacter()
	{
		if (getBuffering() && _currentBufferPosition < getBuffer().length())
		{
			// Read from the buffer
			_currentCharacter = (int)getBuffer().charAt(_currentBufferPosition);
		}
		else
		{
			// No buffer? Peek from the source
			_currentCharacter = getInnerReader().peek();
		}
	}

	private static class BacktrackContext
	{
		private int privateBufferIndex;
		public final int getBufferIndex()
		{
			return privateBufferIndex;
		}
		public final void setBufferIndex(int value)
		{
			privateBufferIndex = value;
		}
		private SourceLocation privateLocation;
		public final SourceLocation getLocation()
		{
			return privateLocation;
		}
		public final void setLocation(SourceLocation value)
		{
			privateLocation = value;
		}
	}
}