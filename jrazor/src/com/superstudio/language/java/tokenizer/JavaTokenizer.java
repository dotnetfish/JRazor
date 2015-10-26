package com.superstudio.language.java.tokenizer;

import com.superstudio.commons.csharpbridge.action.Func;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.jrazor.parser.ParserHelpers;
import com.superstudio.jrazor.parser.syntaxTree.RazorError;
import com.superstudio.jrazor.resources.RazorResources;
import com.superstudio.jrazor.text.ITextDocument;
import com.superstudio.jrazor.text.SourceLocation;
import com.superstudio.jrazor.tokenizer.Tokenizer;
import com.superstudio.language.java.JavaHelpers;
import com.superstudio.language.java.symbols.JavaKeyword;
import com.superstudio.language.java.symbols.JavaSymbol;
import com.superstudio.language.java.symbols.JavaSymbolType;

public class JavaTokenizer extends Tokenizer<JavaSymbol, JavaSymbolType> {
	private java.util.HashMap<Character, Func<JavaSymbolType>> operatorHandlers;

	public JavaTokenizer(ITextDocument source) throws Exception {
		super(source);
		setCurrentState(() -> data());

		operatorHandlers = new java.util.HashMap<Character, Func<JavaSymbolType>>();
		operatorHandlers.put('-', () -> minusOperator());
		operatorHandlers.put('<', () -> lessThanOperator());
		operatorHandlers.put('>', () -> greaterThanOperator());
		operatorHandlers.put('&', createTwoCharOperatorHandler(JavaSymbolType.And, '=', JavaSymbolType.AndAssign, '&',
				JavaSymbolType.DoubleAnd));
		operatorHandlers.put('|', createTwoCharOperatorHandler(JavaSymbolType.Or, '=', JavaSymbolType.OrAssign, '|',
				JavaSymbolType.DoubleOr));
		operatorHandlers.put('+', createTwoCharOperatorHandler(JavaSymbolType.Plus, '=', JavaSymbolType.PlusAssign,
				'+', JavaSymbolType.Increment));
		operatorHandlers.put('=', createTwoCharOperatorHandler(JavaSymbolType.Assign, '=', JavaSymbolType.Equals, '>',
				JavaSymbolType.GreaterThanEqual));
		operatorHandlers.put('!', createTwoCharOperatorHandler(JavaSymbolType.Not, '=', JavaSymbolType.NotEqual));
		operatorHandlers.put('%',
				createTwoCharOperatorHandler(JavaSymbolType.Modulo, '=', JavaSymbolType.ModuloAssign));
		operatorHandlers.put('*',
				createTwoCharOperatorHandler(JavaSymbolType.Star, '=', JavaSymbolType.MultiplyAssign));
		operatorHandlers.put(':', createTwoCharOperatorHandler(JavaSymbolType.Colon, ':', JavaSymbolType.DoubleColon));
		operatorHandlers.put('?',
				createTwoCharOperatorHandler(JavaSymbolType.QuestionMark, '?', JavaSymbolType.NullCoalesce));
		operatorHandlers.put('^', createTwoCharOperatorHandler(JavaSymbolType.Xor, '=', JavaSymbolType.XorAssign));
		operatorHandlers.put('(', () -> JavaSymbolType.LeftParenthesis);
		operatorHandlers.put(')', () -> JavaSymbolType.RightParenthesis);
		operatorHandlers.put('{', () -> JavaSymbolType.LeftBrace);
		operatorHandlers.put('}', () -> JavaSymbolType.RightBrace);
		operatorHandlers.put('[', () -> JavaSymbolType.LeftBracket);
		operatorHandlers.put(']', () -> JavaSymbolType.RightBracket);
		operatorHandlers.put(',', () -> JavaSymbolType.Comma);
		operatorHandlers.put(';', () -> JavaSymbolType.Semicolon);
		operatorHandlers.put('~', () -> JavaSymbolType.Tilde);
		operatorHandlers.put('#', () -> JavaSymbolType.Hash);
	}

	@Override
	protected State getStartState() {
		return () -> data();
	}

	@Override
	public JavaSymbolType getRazorCommentType() {
		return JavaSymbolType.RazorComment;
	}

	@Override
	public JavaSymbolType getRazorCommentTransitionType() {
		return JavaSymbolType.RazorCommentTransition;
	}

	@Override
	public JavaSymbolType getRazorCommentStarType() {
		return JavaSymbolType.RazorCommentStar;
	}

	@Override
	protected JavaSymbol createSymbol(SourceLocation start, String content, JavaSymbolType type,
			Iterable<RazorError> errors) {
		try {
			return new JavaSymbol(start, content, type, errors);
		} catch (ArgumentNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private StateResult<JavaSymbol> data() {
		if (ParserHelpers.isNewLine(getCurrentCharacter())) {

			boolean checkTwoCharNewline = getCurrentCharacter() == '\r';
			takeCurrent();
			if (checkTwoCharNewline && getCurrentCharacter() == '\n') {
				takeCurrent();
			}
			return stay(endSymbol(JavaSymbolType.NewLine));
		} else if (ParserHelpers.isWhitespace(getCurrentCharacter())) {

			takeUntil(c -> !ParserHelpers.isWhitespace(c));
			return stay(endSymbol(JavaSymbolType.WhiteSpace));
		} else if (JavaHelpers.isIdentifierStart(getCurrentCharacter())) {
			return identifier();
		} else if (Character.isDigit(getCurrentCharacter())) {
			return numericLiteral();
		}
		switch (getCurrentCharacter()) {
		case '@':
			return atSymbol();
		case '\'':
			takeCurrent();

			return transition(() -> quotedLiteral('\'', JavaSymbolType.CharacterLiteral));
		case '"':
			takeCurrent();

			return transition(() -> quotedLiteral('"', JavaSymbolType.StringLiteral));
		case '.':
			if (Character.isDigit(peek())) {
				return realLiteral();
			}
			return stay(single(JavaSymbolType.Dot));
		case '/':
			takeCurrent();
			if (getCurrentCharacter() == '/') {
				takeCurrent();
				return singleLineComment();
			} else if (getCurrentCharacter() == '*') {
				takeCurrent();
				return transition(() -> blockComment());
			} else if (getCurrentCharacter() == '=') {
				takeCurrent();
				return stay(endSymbol(JavaSymbolType.DivideAssign));
			} else {
				return stay(endSymbol(JavaSymbolType.Slash));
			}
		default:
			return stay(endSymbol(operator()));
		}
	}

	private StateResult<JavaSymbol> atSymbol() {
		takeCurrent();
		if (getCurrentCharacter() == '"') {
			takeCurrent();
			return transition(() -> verbatimStringLiteral());
		} else if (getCurrentCharacter() == '*') {
			return transition(endSymbol(JavaSymbolType.RazorCommentTransition), () -> afterRazorCommentTransition());
		} else if (getCurrentCharacter() == '@') {
			// Could be escaped comment transition

			return transition(endSymbol(JavaSymbolType.Transition), () -> {
				takeCurrent();
				return transition(endSymbol(JavaSymbolType.Transition), () -> data());
			});
		}
		return stay(endSymbol(JavaSymbolType.Transition));
	}

	private JavaSymbolType operator() {
		char first = getCurrentCharacter();
		takeCurrent();
		Func<JavaSymbolType> handler = null;
		if ((handler = operatorHandlers.get(first)) != null) {
			return handler.execute();
		}
		return JavaSymbolType.Unknown;
	}

	private JavaSymbolType lessThanOperator() {
		if (getCurrentCharacter() == '=') {
			takeCurrent();
			return JavaSymbolType.LessThanEqual;
		}
		return JavaSymbolType.LessThan;
	}

	private JavaSymbolType greaterThanOperator() {
		if (getCurrentCharacter() == '=') {
			takeCurrent();
			return JavaSymbolType.GreaterThanEqual;
		}
		return JavaSymbolType.GreaterThan;
	}

	private JavaSymbolType minusOperator() {
		if (getCurrentCharacter() == '>') {
			takeCurrent();
			return JavaSymbolType.Arrow;
		} else if (getCurrentCharacter() == '-') {
			takeCurrent();
			return JavaSymbolType.Decrement;
		} else if (getCurrentCharacter() == '=') {
			takeCurrent();
			return JavaSymbolType.MinusAssign;
		}
		return JavaSymbolType.Minus;
	}

	private Func<JavaSymbolType> createTwoCharOperatorHandler(JavaSymbolType typeIfOnlyFirst, char second,
			JavaSymbolType typeIfBoth) {

		return () -> {
			if (getCurrentCharacter() == second) {
				takeCurrent();
				return typeIfBoth;
			}
			return typeIfOnlyFirst;
		};
	}

	private Func<JavaSymbolType> createTwoCharOperatorHandler(JavaSymbolType typeIfOnlyFirst, char option1,
			JavaSymbolType typeIfOption1, char option2, JavaSymbolType typeIfOption2) {

		return () -> {
			if (getCurrentCharacter() == option1) {
				takeCurrent();
				return typeIfOption1;
			} else if (getCurrentCharacter() == option2) {
				takeCurrent();
				return typeIfOption2;
			}
			return typeIfOnlyFirst;
		};
	}

	private StateResult<JavaSymbol> verbatimStringLiteral() {

		takeUntil(c -> c == '"');
		if (getCurrentCharacter() == '"') {
			takeCurrent();
			if (getCurrentCharacter() == '"') {
				takeCurrent();
				// Stay in the literal, this is an escaped "
				return stay();
			}
		} else if (getEndOfFile()) {
			getCurrentErrors().add(new RazorError(RazorResources.getParseError_Unterminated_String_Literal(),
					getCurrentStart().clone()));
		}
		return transition(endSymbol(JavaSymbolType.StringLiteral), () -> data());
	}

	private StateResult<JavaSymbol> quotedLiteral(char quote, JavaSymbolType literalType) {

		takeUntil(c -> c == '\\' || c == quote || ParserHelpers.isNewLine(c));
		if (getCurrentCharacter() == '\\') {
			takeCurrent(); // Take the '\'

			// If the next char is the same quote that started this
			if (getCurrentCharacter() == quote || getCurrentCharacter() == '\\') {
				takeCurrent(); // Take it so that we don't prematurely end the
								// literal.
			}
			return stay();
		} else if (getEndOfFile() || ParserHelpers.isNewLine(getCurrentCharacter())) {
			getCurrentErrors().add(new RazorError(RazorResources.getParseError_Unterminated_String_Literal(),
					getCurrentStart().clone()));
		} else {
			takeCurrent(); // No-op if at EOF
		}
		return transition(endSymbol(literalType), () -> data());
	}

	
	private StateResult<JavaSymbol> blockComment() {

		takeUntil(c -> c == '*');
		if (getEndOfFile()) {
			getCurrentErrors().add(new RazorError(RazorResources.getParseError_BlockComment_Not_Terminated(),
					getCurrentStart().clone()));
			return transition(endSymbol(JavaSymbolType.Comment), () -> data());
		}
		if (getCurrentCharacter() == '*') {
			takeCurrent();
			if (getCurrentCharacter() == '/') {
				takeCurrent();
				return transition(endSymbol(JavaSymbolType.Comment), () -> data());
			}
		}
		return stay();
	}

	
	private StateResult<JavaSymbol> singleLineComment() {

		takeUntil(c -> ParserHelpers.isNewLine(c));
		return stay(endSymbol(JavaSymbolType.Comment));
	}

	
	private StateResult<JavaSymbol> numericLiteral() {
		if (takeAll("0x", true)) {
			return hexLiteral();
		} else {
			return decimalLiteral();
		}
	}

	private StateResult<JavaSymbol> hexLiteral() {

		takeUntil(c -> !ParserHelpers.isHexDigit(c));
		takeIntegerSuffix();
		return stay(endSymbol(JavaSymbolType.IntegerLiteral));
	}

	private StateResult<JavaSymbol> decimalLiteral() {

		takeUntil(c -> !Character.isDigit(c));
		if (getCurrentCharacter() == '.' && Character.isDigit(peek())) {
			return realLiteral();
		} else if (JavaHelpers.isRealLiteralSuffix(getCurrentCharacter()) || getCurrentCharacter() == 'E'
				|| getCurrentCharacter() == 'e') {
			return realLiteralExponentPart();
		} else {
			takeIntegerSuffix();
			return stay(endSymbol(JavaSymbolType.IntegerLiteral));
		}
	}

	private StateResult<JavaSymbol> realLiteralExponentPart() {
		if (getCurrentCharacter() == 'E' || getCurrentCharacter() == 'e') {
			takeCurrent();
			if (getCurrentCharacter() == '+' || getCurrentCharacter() == '-') {
				takeCurrent();
			}

			takeUntil(c -> !Character.isDigit(c));
		}
		if (JavaHelpers.isRealLiteralSuffix(getCurrentCharacter())) {
			takeCurrent();
		}
		return stay(endSymbol(JavaSymbolType.RealLiteral));
	}

	
	private StateResult<JavaSymbol> realLiteral() {
		assertCurrent('.');
		takeCurrent();
		assert Character.isDigit(getCurrentCharacter());

		takeUntil(c -> !Character.isDigit(c));
		return realLiteralExponentPart();
	}

	private void takeIntegerSuffix() {
		if (Character.toLowerCase(getCurrentCharacter()) == 'u') {
			takeCurrent();
			if (Character.toLowerCase(getCurrentCharacter()) == 'l') {
				takeCurrent();
			}
		} else if (Character.toLowerCase(getCurrentCharacter()) == 'l') {
			takeCurrent();
			if (Character.toLowerCase(getCurrentCharacter()) == 'u') {
				takeCurrent();
			}
		}
	}

	
	private StateResult<JavaSymbol> identifier() {
		assert JavaHelpers.isIdentifierStart(getCurrentCharacter());
		takeCurrent();

		takeUntil(c -> !JavaHelpers.isIdentifierPart(c));
		JavaSymbol sym = null;
		if (getHaveContent()) {
			String currentChars = getBuffer().toString();
			JavaKeyword kwd = JavaKeywordDetector.symbolTypeForIdentifier(currentChars);
			JavaSymbolType type = JavaSymbolType.Identifier;
			if (kwd != null) {
				type = JavaSymbolType.Keyword;
			}
			JavaSymbol tempVar = null;
			try {
				tempVar = new JavaSymbol(getCurrentStart().clone(), getBuffer().toString(), type);
			} catch (ArgumentNullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tempVar.setKeyword(kwd);
			sym = tempVar;
		}
		startSymbol();
		return stay(sym);
	}
}