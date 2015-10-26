package com.superstudio.jrazor.tokenizer;

import java.util.Iterator;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.jrazor.parser.ParserHelpers;
import com.superstudio.jrazor.parser.syntaxTree.RazorError;
import com.superstudio.jrazor.text.ITextDocument;
import com.superstudio.jrazor.text.SeekableTextReader;
import com.superstudio.jrazor.text.SourceLocation;
import com.superstudio.jrazor.tokenizer.symbols.HtmlSymbol;
import com.superstudio.jrazor.tokenizer.symbols.HtmlSymbolType;

public class HtmlTokenizer extends Tokenizer<HtmlSymbol, HtmlSymbolType> {
	private static final char TransitionChar = '@';

	public HtmlTokenizer(ITextDocument source) throws Exception {
		super(source);
		setCurrentState(() -> data());
	}

	@Override
	protected State getStartState() {
		return () -> data();
	}

	@Override
	public HtmlSymbolType getRazorCommentType() {
		return HtmlSymbolType.RazorComment;
	}

	@Override
	public HtmlSymbolType getRazorCommentTransitionType() {
		return HtmlSymbolType.RazorCommentTransition;
	}

	@Override
	public HtmlSymbolType getRazorCommentStarType() {
		return HtmlSymbolType.RazorCommentStar;
	}

	public static Iterable<HtmlSymbol> tokenize(String content) throws Exception {

		SeekableTextReader reader = new SeekableTextReader(content);
		final HtmlTokenizer tok = new HtmlTokenizer(reader);

		return new Iterable<HtmlSymbol>() {
			
			@Override
			public Iterator<HtmlSymbol> iterator() {

				return new Iterator<HtmlSymbol>() {
					private HtmlSymbol current = null;

					@Override
					public boolean hasNext() {

						boolean result = (current = (HtmlSymbol) tok.nextSymbol()) != null;
						if (!result)
							reader.close();
						return result;
					}

					@Override
					public HtmlSymbol next() {

						return current;
					}

				};
			}

		};

	}

	@Override
	protected HtmlSymbol createSymbol(SourceLocation start, String content, HtmlSymbolType type,
			Iterable<RazorError> errors) throws ArgumentNullException {
		return new HtmlSymbol(start, content, type, errors);
	}

	private StateResult<HtmlSymbol> data() {
		if (ParserHelpers.isWhitespace(getCurrentCharacter())) {
			return stay(whitespace());
		} else if (ParserHelpers.isNewLine(getCurrentCharacter())) {
			return stay(newline());
		} else if (getCurrentCharacter() == TransitionChar) {
			takeCurrent();
			if (getCurrentCharacter() == '*') {
				return transition(endSymbol(HtmlSymbolType.RazorCommentTransition),
						() -> afterRazorCommentTransition());
			} else if (getCurrentCharacter() == TransitionChar) {
				// Could be escaped comment transition

				return transition(endSymbol(HtmlSymbolType.Transition), () -> {
					takeCurrent();
					return transition(endSymbol(HtmlSymbolType.Transition), () -> data());
				});
			}
			return stay(endSymbol(HtmlSymbolType.Transition));
		} else if (atSymbol()) {
			return stay(symbol());
		} else {
			return transition(() -> text());
		}
	}

	private StateResult<HtmlSymbol> text() {
		char prev = '\0';
		while (!getEndOfFile() && !ParserHelpers.isWhitespaceOrNewLine(getCurrentCharacter()) && !atSymbol()) {
			prev = getCurrentCharacter();
			takeCurrent();
		}

		if (getCurrentCharacter() == TransitionChar) {
			char next = peek();
			if (ParserHelpers.isLetterOrDecimalDigit(prev) && ParserHelpers.isLetterOrDecimalDigit(next)) {
				takeCurrent(); // Take the "@"
				return stay(); // Stay in the Text state
			}
		}

		// Output the Text token and return to the Data state to tokenize the
		// next character (if there is one)
		return transition(endSymbol(HtmlSymbolType.Text), () -> data());
	}

	private HtmlSymbol symbol() {
		assert atSymbol();
		char sym = getCurrentCharacter();
		takeCurrent();
		switch (sym) {
		case '<':
			return endSymbol(HtmlSymbolType.OpenAngle);
		case '!':
			return endSymbol(HtmlSymbolType.Bang);
		case '/':
			return endSymbol(HtmlSymbolType.Solidus);
		case '?':
			return endSymbol(HtmlSymbolType.QuestionMark);
		case '[':
			return endSymbol(HtmlSymbolType.LeftBracket);
		case '>':
			return endSymbol(HtmlSymbolType.CloseAngle);
		case ']':
			return endSymbol(HtmlSymbolType.RightBracket);
		case '=':
			return endSymbol(HtmlSymbolType.Equals);
		case '"':
			return endSymbol(HtmlSymbolType.DoubleQuote);
		case '\'':
			return endSymbol(HtmlSymbolType.SingleQuote);
		case '-':
			assert getCurrentCharacter() == '-';
			takeCurrent();
			return endSymbol(HtmlSymbolType.DoubleHyphen);
		default:
			//// Debug.Fail("Unexpected symbol!");
			return endSymbol(HtmlSymbolType.Unknown);
		}
	}

	private HtmlSymbol whitespace() {
		while (ParserHelpers.isWhitespace(getCurrentCharacter())) {
			takeCurrent();
		}
		return endSymbol(HtmlSymbolType.WhiteSpace);
	}

	private HtmlSymbol newline() {
		assert ParserHelpers.isNewLine(getCurrentCharacter());

		boolean checkTwoCharNewline = getCurrentCharacter() == '\r';
		takeCurrent();
		if (checkTwoCharNewline && getCurrentCharacter() == '\n') {
			takeCurrent();
		}
		return endSymbol(HtmlSymbolType.NewLine);
	}

	private boolean atSymbol() {
		return getCurrentCharacter() == '<' || getCurrentCharacter() == '<' || getCurrentCharacter() == '!'
				|| getCurrentCharacter() == '/' || getCurrentCharacter() == '?' || getCurrentCharacter() == '['
				|| getCurrentCharacter() == '>' || getCurrentCharacter() == ']' || getCurrentCharacter() == '='
				|| getCurrentCharacter() == '"' || getCurrentCharacter() == '\'' || getCurrentCharacter() == '@'
				|| (getCurrentCharacter() == '-' && peek() == '-');
	}
}