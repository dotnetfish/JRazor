package com.superstudio.web.razor.tokenizer;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.web.RazorResources;
import com.superstudio.web.razor.StateMachine;
import com.superstudio.web.razor.parser.ParserHelpers;
import com.superstudio.web.razor.parser.syntaxTree.RazorError;
import com.superstudio.web.razor.text.ITextDocument;
import com.superstudio.web.razor.text.LookaheadToken;
import com.superstudio.web.razor.text.SourceLocation;
import com.superstudio.web.razor.text.TextDocumentReader;
import com.superstudio.web.razor.tokenizer.symbols.SymbolBase;

import java.util.function.Function;
import java.util.function.Predicate;






/**
 * @author cloudartisan
 */
public abstract class Tokenizer<TSymbol extends SymbolBase<TSymbolType>, TSymbolType> extends StateMachine<TSymbol>
implements ITokenizer 
{
	


	//[SuppressMessage("Microsoft.Reliability", "CA2000:dispose objects before losing scope", Justification = "TextDocumentReader does not require disposal")]
	protected Tokenizer(ITextDocument source) throws Exception
	{
		if (source == null)
		{
			//throw new ArgumentNullException("source");
		}
		setSource(new TextDocumentReader(source));
		setBuffer(new StringBuilder());
		setCurrentErrors(new java.util.ArrayList<RazorError>());
		startSymbol();
	}

	private TextDocumentReader privateSource;
	public final TextDocumentReader getSource()
	{
		return privateSource;
	}
	private void setSource(TextDocumentReader value)
	{
		privateSource = value;
	}

	private StringBuilder privateBuffer;
	protected final StringBuilder getBuffer()
	{
		return privateBuffer;
	}
	private void setBuffer(StringBuilder value)
	{
		privateBuffer = value;
	}

	protected final boolean getEndOfFile()
	{
		return getSource().peek() == -1;
	}

	private java.util.List<RazorError> privateCurrentErrors;
	protected final java.util.List<RazorError> getCurrentErrors()
	{
		return privateCurrentErrors;
	}
	private void setCurrentErrors(java.util.List<RazorError> value)
	{
		privateCurrentErrors = value;
	}

	public abstract TSymbolType getRazorCommentStarType();
	public abstract TSymbolType getRazorCommentType();
	public abstract TSymbolType getRazorCommentTransitionType();

	protected final boolean getHaveContent()
	{
		return getBuffer().length() > 0;
	}

	protected final char getCurrentCharacter()
	{
		int peek = getSource().peek();
		return peek == -1 ? '\0' : (char)peek;
	}

	protected final SourceLocation getCurrentLocation()
	{
		return getSource().getLocation();
	}

	private SourceLocation privateCurrentStart;
	protected final SourceLocation getCurrentStart()
	{
		return privateCurrentStart;
	}
	private void setCurrentStart(SourceLocation value)
	{
		privateCurrentStart = value;
	}

	@Override
    public TSymbol nextSymbol()
	{
		// post-Condition: Buffer should be empty at the start of next()
		assert getBuffer().length() == 0;
		startSymbol();

		if (getEndOfFile())
		{
			return null;
		}
		TSymbol sym = turn();

		// post-Condition: Buffer should be empty at the end of next()
		assert getBuffer().length() == 0;

		return sym;
	}

	public final void reset()
	{
		setCurrentState(getStartState());
	}

	protected abstract TSymbol createSymbol(SourceLocation start, String content, TSymbolType type,
											Iterable<RazorError> errors) throws ArgumentNullException;

	protected final TSymbol single(TSymbolType type)
	{
		takeCurrent();
		return endSymbol(type);
	}

	protected final boolean TakeString(String input, boolean caseSensitive)
	{
		int position = 0;

		Function<Character, Character> charFilter = c -> c;
		if (caseSensitive)
		{
			//charFilter = Character.toLowerCase;
			charFilter=c->Character.toLowerCase(c);
		}
		while (!getEndOfFile() && position < input.length() && charFilter.apply(getCurrentCharacter()) == charFilter.apply(input.charAt(position++)))
		{
			takeCurrent();
		}
		return position == input.length();
	}

	protected final void startSymbol()
	{
		getBuffer().delete(0,getBuffer().length());
		setCurrentStart(getCurrentLocation().clone());
		getCurrentErrors().clear();
	}

	protected final TSymbol endSymbol(TSymbolType type)
	{
		return endSymbol(getCurrentStart().clone(), type);
	}

	protected final TSymbol endSymbol(SourceLocation start, TSymbolType type)
	{
		TSymbol sym = null;
		if (getHaveContent())
		{
			try {
				sym = createSymbol(start, getBuffer().toString(), type, getCurrentErrors());
			} catch (ArgumentNullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		startSymbol();
		return sym;
	}

	protected final void resumeSymbol(TSymbol previous) throws Exception
	{
		// Verify the symbol can be resumed
		if (previous.getStart().getAbsoluteIndex() + previous.getContent().length() != getCurrentStart().getAbsoluteIndex())
		{
			throw new InvalidOperationException(RazorResources.getResource(RazorResources.Tokenizer_CannotResumeSymbolUnlessIsPrevious));
		}

		// reset the start point
		setCurrentStart(previous.getStart());

		// Capture the current buffer content
		String newContent = getBuffer().toString();

		// Clear the buffer, then put the old content back and add the new content to the end
		//getBuffer().delete(0,-1);
		getBuffer().delete(0,getBuffer().length());
		getBuffer().append(previous.getContent());
		getBuffer().append(newContent);
	}

	protected final boolean takeUntil(Predicate<Character> predicate)
	{
		// Take all the characters up to the end character
		while (!getEndOfFile() && !predicate.test(getCurrentCharacter()))
		{
			takeCurrent();
		}

		// Why did we end?
		return !getEndOfFile();
	}

	protected final Predicate<Character> charOrWhiteSpace(char character)
	{

		return c -> c == character || ParserHelpers.isWhitespace(c) || ParserHelpers.isNewLine(c);
	}

	protected final void takeCurrent()
	{
		if (getEndOfFile())
		{
			return;
		} // No-op
		getBuffer().append(getCurrentCharacter());
		moveNext();
	}

	protected final void moveNext()
	{

//#if //Debug
		_read.append(getCurrentCharacter());
//#endif
		getSource().read();
	}

	protected final boolean takeAll(String expected, boolean caseSensitive)
	{
		return lookahead(expected, true, caseSensitive);
	}

	protected final boolean at(String expected, boolean caseSensitive)
	{
		return lookahead(expected, false, caseSensitive);
	}

	protected final char peek()
	{

//		using (LookaheadToken lookahead = Source.beginLookahead())
		LookaheadToken lookahead = getSource().beginLookahead();
		try
		{
			moveNext();
			return getCurrentCharacter();
		}
		finally
		{
			lookahead.dispose();
		}
	}

	protected final StateResult afterRazorCommentTransition()
	{
		if (getCurrentCharacter() != '*')
		{
			// We've been moved since last time we were asked for a symbol... reset the state
			return transition(getStartState());
		}
		assertCurrent('*');
		takeCurrent();
		return transition(endSymbol(getRazorCommentStarType()), ()-> razorCommentBody());
	}

	protected final StateResult razorCommentBody()
	{

		takeUntil(c -> c == '*');
		if (getCurrentCharacter() == '*')
		{
			char star = getCurrentCharacter();
			SourceLocation start = getCurrentLocation().clone();
			moveNext();
			if (!getEndOfFile() && getCurrentCharacter() == '@')
			{

				State next = () ->
				{
					getBuffer().append(star);
							// We've been moved since last time we were asked for a symbol... reset the state

					return transition(endSymbol(start, getRazorCommentStarType()), () ->
					{
						if (getCurrentCharacter() != '@')
						{
							return transition(getStartState());
						}
						takeCurrent();
						return transition(endSymbol(getRazorCommentTransitionType()), getStartState());
					}
				   );
				};

				if (getHaveContent())
				{
					return transition(endSymbol(getRazorCommentType()), next);
				}
				else
				{
					return transition(next);
				}
			}
			else
			{
				getBuffer().append(star);
				return stay();
			}
		}
		return transition(endSymbol(getRazorCommentType()), getStartState());
	}

	private boolean lookahead(String expected, boolean takeIfMatch, boolean caseSensitive)
	{

		Function<Character, Character> filter = c -> c;
		if (!caseSensitive)
		{
			filter = c->Character.toLowerCase(c);
			//filter = Character.toLowerCase(ch);
		}

		if (expected.length() == 0 || filter.apply(getCurrentCharacter()) != filter.apply(expected.charAt(0)))
		{
			return false;
		}

		// Capture the current buffer content in case we have to backtrack
		String oldBuffer = null;
		if (takeIfMatch)
		{
			getBuffer().toString();
		}


//		using (LookaheadToken lookahead = Source.beginLookahead())
		LookaheadToken lookahead = getSource().beginLookahead();
		try
		{
			for (int i = 0; i < expected.length(); i++)
			{
				if (!filter.apply(getCurrentCharacter()).equals(filter.apply(expected.charAt(i))))
				{
					if (takeIfMatch)
					{
						// Clear the buffer and put the old buffer text back
						//getBuffer().delete(0,-1);
						getBuffer().delete(0,getBuffer().length());
						getBuffer().append(oldBuffer);
					}
					// Return without accepting lookahead (thus rejecting it)
					return false;
				}
				if (takeIfMatch)
				{
					takeCurrent();
				}
				else
				{
					moveNext();
				}
			}
			if (takeIfMatch)
			{
				lookahead.accept();
			}
		}
		finally
		{
			lookahead.dispose();
		}
		return true;
	}


	//[SuppressMessage("Microsoft.Performance", "CA1822:MarkMembersAsStatic", Justification = "This only occurs in Release builds, where this method is empty by design"), Conditional("//Debug")]
	public final void assertCurrent(char current)
	{
		//assert (getCurrentCharacter() == current, "CurrentCharacter Assumption violated", "Assumed that the current character would be {0}, but it is actually {1}", current, getCurrentCharacter());
	}

	


	private StringBuilder _read = new StringBuilder();

	public final String getDebugDisplay()
	{
		return String.format("[%s] [%s] [%s]", _read.toString(),
				getCurrentCharacter(), getRemaining());
	}

	public final String getRemaining()
	{
		String remaining = getSource().readToEnd();
		getSource().seek(-remaining.length());
		return remaining;
	}
}