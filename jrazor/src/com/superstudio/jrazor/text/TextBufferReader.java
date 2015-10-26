package com.superstudio.jrazor.text;

import com.superstudio.commons.IDisposable;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.jrazor.utils.DisposableAction;




public class TextBufferReader extends LookaheadTextReader
{
	private java.util.Stack<BacktrackContext> _bookmarks = new java.util.Stack<BacktrackContext>();
	private SourceLocationTracker _tracker = new SourceLocationTracker();

	public TextBufferReader(ITextBuffer buffer) throws ArgumentNullException
	{
		if (buffer == null)
		{
			throw new ArgumentNullException("buffer");
		}

		setInnerBuffer(buffer);
	}

	private ITextBuffer privateInnerBuffer;
	public final ITextBuffer getInnerBuffer()
	{
		return privateInnerBuffer;
	}
	public final void setInnerBuffer(ITextBuffer value)
	{
		privateInnerBuffer = value;
	}

	@Override
	public SourceLocation getCurrentLocation()
	{
		return _tracker.getCurrentLocation();
	}

	@Override
	public int peek()
	{
		return getInnerBuffer().peek();
	}

	@Override
	public int read()
	{
		int read = getInnerBuffer().read();
		if (read != -1)
		{
			char nextChar = '\0';
			int next = peek();
			if (next != -1)
			{
				nextChar = (char)next;
			}
			_tracker.UpdateLocation((char)read, nextChar);
		}
		return read;
	}

	
	protected void dispose(boolean disposing)
	{
		if (disposing)
		{
			ITextBuffer tempVar = getInnerBuffer();
			IDisposable disposable = (IDisposable)((tempVar instanceof IDisposable) ? tempVar : null);
			if (disposable != null)
			{
				disposable.dispose();
			}
		}
		//super.dispose(disposing);
	}

	@Override
	public IDisposable beginLookahead()
	{
		BacktrackContext tempVar = new BacktrackContext();
		tempVar.setLocation(getCurrentLocation());
		BacktrackContext context = tempVar;
		_bookmarks.push(context);
		return new DisposableAction(() ->
		{
			EndLookahead(context);
		}
	   );
	}

	@Override
	public void cancelBacktrack()
	{
		if (_bookmarks.empty())
		{
			//throw new InvalidOperationException(RazorResources.getCancelBacktrack_Must_Be_Called_Within_Lookahead());
		}
		_bookmarks.pop();
	}

	private void EndLookahead(BacktrackContext context)
	{
		if (_bookmarks.size() > 0 && _bookmarks.peek().equals(context))
		{
			// Backtrack wasn't cancelled, so pop it
			_bookmarks.pop();

			// Set the new current location
			_tracker.setCurrentLocation(context.getLocation().clone());
			getInnerBuffer().setPosition(context.getLocation().getAbsoluteIndex());
		}
	}

	/** 
	 Need a class for reference equality to support cancelling backtrack.
	 
	*/
	private static class BacktrackContext
	{
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