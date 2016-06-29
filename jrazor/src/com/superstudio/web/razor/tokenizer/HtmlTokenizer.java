package com.superstudio.web.razor.tokenizer;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.web.razor.parser.ParserHelpers;
import com.superstudio.web.razor.parser.syntaxTree.RazorError;
import com.superstudio.web.razor.text.ITextDocument;
import com.superstudio.web.razor.text.SeekableTextReader;
import com.superstudio.web.razor.text.SourceLocation;
import com.superstudio.web.razor.tokenizer.symbols.HtmlSymbol;
import com.superstudio.web.razor.tokenizer.symbols.HtmlSymbolType;

import java.util.Iterator;



// Tokenizer _loosely_ based on http://dev.w3.org/html5/spec/Overview.html#tokenization
public class HtmlTokenizer extends Tokenizer<HtmlSymbol, HtmlSymbolType>
{
	private static final char TransitionChar = '@';

	public HtmlTokenizer(ITextDocument source) throws Exception
	{
		super(source);
		setCurrentState(()->Data());
	}

	@Override
	protected State getStartState()
	{
		return ()->Data();
	}

	@Override
	public HtmlSymbolType getRazorCommentType()
	{

		return HtmlSymbolType.RazorComment;
	}

	@Override
	public HtmlSymbolType getRazorCommentTransitionType()
	{
		return HtmlSymbolType.RazorCommentTransition;
	}

	@Override
	public HtmlSymbolType getRazorCommentStarType()
	{
		return HtmlSymbolType.RazorCommentStar;
	}

	public static Iterable<HtmlSymbol> tokenize(String content) throws Exception
	{
		SeekableTextReader reader = new SeekableTextReader(content);
		final HtmlTokenizer  tok=new HtmlTokenizer(reader);
		
		//if(tok==null)return Collections.emptyList();
		
		return new Iterable<HtmlSymbol>(){
			//private final SeekableTextReader reader = new SeekableTextReader(content);
			@Override
			public Iterator<HtmlSymbol> iterator() {
					return new Iterator<HtmlSymbol>(){
					private HtmlSymbol current=null;
					@Override
					public boolean hasNext() {
						boolean result=(current = tok.nextSymbol()) != null;
						if(!result)reader.close();
						return result;
					}

					@Override
					public HtmlSymbol next() {
						return current;
					}
					
				};
			}
			
		};
		/*
		try
		{
			HtmlTokenizer tok = new HtmlTokenizer(reader);
			HtmlSymbol sym;
			while ((sym = (HtmlSymbol) tok.nextSymbol()) != null)
			{

				yield return sym;
			}
		}
		finally
		{
			reader.dispose();
		}*/
	}

	protected HtmlSymbol createSymbol(SourceLocation start, String content, HtmlSymbolType type,
									  Iterable<RazorError> errors) throws ArgumentNullException
	{
		return new HtmlSymbol(start, content, type, errors);
	}

	// http://dev.w3.org/html5/spec/Overview.html#data-state
	private StateResult Data()
	{
		if (ParserHelpers.isWhitespace(getCurrentCharacter()))
		{
			return stay(Whitespace());
		}
		else if (ParserHelpers.isNewLine(getCurrentCharacter()))
		{
			return stay(Newline());
		}
		else if (getCurrentCharacter() == '@')
		{
			TakeCurrent();
			if (getCurrentCharacter() == '*')
			{
				return transition(EndSymbol(HtmlSymbolType.RazorCommentTransition), ()->AfterRazorCommentTransition());
			}
			else if (getCurrentCharacter() == '@')
			{
				// Could be escaped comment transition

				return transition(EndSymbol(HtmlSymbolType.Transition), () ->
				{
					TakeCurrent();
					return transition(EndSymbol(HtmlSymbolType.Transition), ()->Data());
				}
			   );
			}
			return stay(EndSymbol(HtmlSymbolType.Transition));
		}
		else if (AtSymbol())
		{
			return stay(Symbol());
		}
		else
		{
			return transition(()->Text());
		}
	}

	private StateResult Text()
	{
		char prev = '\0';
		while (!getEndOfFile() && !ParserHelpers.isWhitespaceOrNewLine(getCurrentCharacter()) && !AtSymbol())
		{
			prev = getCurrentCharacter();
			TakeCurrent();
		}

		if (getCurrentCharacter() == '@')
		{
			char next = Peek();
			if (ParserHelpers.isLetterOrDecimalDigit(prev) && ParserHelpers.isLetterOrDecimalDigit(next))
			{
				TakeCurrent(); // Take the "@"
				return stay(); // stay in the Text state
			}
		}

		// Output the Text token and return to the Data state to tokenize the next character (if there is one)
		return transition(EndSymbol(HtmlSymbolType.Text), ()->Data());
	}

	private HtmlSymbol Symbol()
	{
		assert AtSymbol();
		char sym = getCurrentCharacter();
		TakeCurrent();
		switch (sym)
		{
			case '<':
				return EndSymbol(HtmlSymbolType.OpenAngle);
			case '!':
				return EndSymbol(HtmlSymbolType.Bang);
			case '/':
				return EndSymbol(HtmlSymbolType.Solidus);
			case '?':
				return EndSymbol(HtmlSymbolType.QuestionMark);
			case '[':
				return EndSymbol(HtmlSymbolType.LeftBracket);
			case '>':
				return EndSymbol(HtmlSymbolType.CloseAngle);
			case ']':
				return EndSymbol(HtmlSymbolType.RightBracket);
			case '=':
				return EndSymbol(HtmlSymbolType.Equals);
			case '"':
				return EndSymbol(HtmlSymbolType.DoubleQuote);
			case '\'':
				return EndSymbol(HtmlSymbolType.SingleQuote);
			case '-':
				assert getCurrentCharacter() == '-';
				TakeCurrent();
				return EndSymbol(HtmlSymbolType.DoubleHyphen);
			default:
				////Debug.Fail("Unexpected symbol!");
				return EndSymbol(HtmlSymbolType.Unknown);
		}
	}

	private HtmlSymbol Whitespace()
	{
		while (ParserHelpers.isWhitespace(getCurrentCharacter()))
		{
			TakeCurrent();
		}
		return EndSymbol(HtmlSymbolType.WhiteSpace);
	}

	private HtmlSymbol Newline()
	{
		assert ParserHelpers.isNewLine(getCurrentCharacter());
		// CSharp Spec §2.3.1
		boolean checkTwoCharNewline = getCurrentCharacter() == '\r';
		TakeCurrent();
		if (checkTwoCharNewline && getCurrentCharacter() == '\n')
		{
			TakeCurrent();
		}
		return EndSymbol(HtmlSymbolType.NewLine);
	}

	private boolean AtSymbol()
	{
		return getCurrentCharacter() == '<' || getCurrentCharacter() == '<' || getCurrentCharacter() == '!' || getCurrentCharacter() == '/' || getCurrentCharacter() == '?' || getCurrentCharacter() == '[' || getCurrentCharacter() == '>' || getCurrentCharacter() == ']' || getCurrentCharacter() == '=' || getCurrentCharacter() == '"' || getCurrentCharacter() == '\'' || getCurrentCharacter() == '@' || (getCurrentCharacter() == '-' && Peek() == '-');
	}
}