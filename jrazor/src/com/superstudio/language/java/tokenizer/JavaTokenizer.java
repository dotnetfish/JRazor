package com.superstudio.language.java.tokenizer;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.language.java.JavaHelpers;
import com.superstudio.language.java.symbols.JavaKeyword;
import com.superstudio.language.java.symbols.JavaSymbol;
import com.superstudio.language.java.symbols.JavaSymbolType;
import com.superstudio.web.RazorResources;
import com.superstudio.web.razor.parser.ParserHelpers;
import com.superstudio.web.razor.parser.syntaxTree.RazorError;
import com.superstudio.web.razor.text.ITextDocument;
import com.superstudio.web.razor.text.SourceLocation;
import com.superstudio.web.razor.tokenizer.Tokenizer;

import java.util.function.Supplier;


public class JavaTokenizer extends Tokenizer<JavaSymbol, JavaSymbolType>
{
	private java.util.HashMap<Character, Supplier<JavaSymbolType>> _operatorHandlers;
	private static final char BACKSLASH='\\';
	private static final char ASTERISK ='*';

	public JavaTokenizer(ITextDocument source) throws Exception
	{
		super(source);
		setCurrentState(()->Data());

		_operatorHandlers = new java.util.HashMap<Character, Supplier<JavaSymbolType>>();
		_operatorHandlers.put('-',()-> minusOperator());
		_operatorHandlers.put('<', ()-> lessThanOperator());
		_operatorHandlers.put('>',()-> greaterThanOperator());
		_operatorHandlers.put('&', createTwoCharOperatorHandler(JavaSymbolType.And, '=', JavaSymbolType.AndAssign, '&', JavaSymbolType.DoubleAnd));
		_operatorHandlers.put('|', createTwoCharOperatorHandler(JavaSymbolType.Or, '=', JavaSymbolType.OrAssign, '|', JavaSymbolType.DoubleOr));
		_operatorHandlers.put('+', createTwoCharOperatorHandler(JavaSymbolType.Plus, '=', JavaSymbolType.PlusAssign, '+', JavaSymbolType.Increment));
		_operatorHandlers.put('=', createTwoCharOperatorHandler(JavaSymbolType.Assign, '=', JavaSymbolType.Equals, '>', JavaSymbolType.GreaterThanEqual));
		_operatorHandlers.put('!', createTwoCharOperatorHandler(JavaSymbolType.Not, '=', JavaSymbolType.NotEqual));
		_operatorHandlers.put('%', createTwoCharOperatorHandler(JavaSymbolType.Modulo, '=', JavaSymbolType.ModuloAssign));
		_operatorHandlers.put('*', createTwoCharOperatorHandler(JavaSymbolType.Star, '=', JavaSymbolType.MultiplyAssign));
		_operatorHandlers.put(':', createTwoCharOperatorHandler(JavaSymbolType.Colon, ':', JavaSymbolType.DoubleColon));
		_operatorHandlers.put('?', createTwoCharOperatorHandler(JavaSymbolType.QuestionMark, '?', JavaSymbolType.NullCoalesce));
		_operatorHandlers.put('^', createTwoCharOperatorHandler(JavaSymbolType.Xor, '=', JavaSymbolType.XorAssign));
		_operatorHandlers.put('(', () -> JavaSymbolType.LeftParenthesis);
		_operatorHandlers.put(')', () -> JavaSymbolType.RightParenthesis);
		_operatorHandlers.put('{', () -> JavaSymbolType.LeftBrace);
		_operatorHandlers.put('}', () -> JavaSymbolType.RightBrace);
		_operatorHandlers.put('[', () -> JavaSymbolType.LeftBracket);
		_operatorHandlers.put(']', () -> JavaSymbolType.RightBracket);
		_operatorHandlers.put(',', () -> JavaSymbolType.Comma);
		_operatorHandlers.put(';', () -> JavaSymbolType.Semicolon);
		_operatorHandlers.put('~', () -> JavaSymbolType.Tilde);
		_operatorHandlers.put('#', () -> JavaSymbolType.Hash);
	}

	@Override
	protected State getStartState()
	{
		return ()->Data();
	}

	@Override
	public JavaSymbolType getRazorCommentType()
	{
		return JavaSymbolType.RazorComment;
	}

	@Override
	public JavaSymbolType getRazorCommentTransitionType()
	{
		return JavaSymbolType.RazorCommentTransition;
	}

	@Override
	public JavaSymbolType getRazorCommentStarType()
	{
		return JavaSymbolType.RazorCommentStar;
	}

	@Override
	protected JavaSymbol createSymbol(SourceLocation start, String content,
									  JavaSymbolType type, Iterable<RazorError> errors)
	{
		try {
			return new JavaSymbol(start, content, type, errors);
		} catch (ArgumentNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private StateResult Data()
	{
		if (ParserHelpers.isNewLine(getCurrentCharacter()))
		{
			// CSharp Spec §2.3.1
			boolean checkTwoCharNewline = getCurrentCharacter() == '\r';
			takeCurrent();
			if (checkTwoCharNewline && getCurrentCharacter() == '\n')
			{
				takeCurrent();
			}
			return stay(endSymbol(JavaSymbolType.NewLine));
		}
		else if (ParserHelpers.isWhitespace(getCurrentCharacter()))
		{
			// CSharp Spec §2.3.3

			takeUntil(c -> !ParserHelpers.isWhitespace(c));
			return stay(endSymbol(JavaSymbolType.WhiteSpace));
		}
		else if (JavaHelpers.isIdentifierStart(getCurrentCharacter()))
		{
			return identifier();
		}
		else if (Character.isDigit(getCurrentCharacter()))
		{
			return numericLiteral();
		}
		switch (getCurrentCharacter())
		{
			case '@':
				return atSymbol();
			case '\'':
				takeCurrent();

				return transition(() -> quotedLiteral('\'', JavaSymbolType.CharacterLiteral));
			case '"':
				takeCurrent();

				return transition(() -> quotedLiteral('"', JavaSymbolType.StringLiteral));
			case '.':
				if (Character.isDigit(peek()))
				{
					return realLiteral();
				}
				return stay(single(JavaSymbolType.Dot));
			case '/':
				takeCurrent();
				if (getCurrentCharacter() == '/')
				{
					takeCurrent();
					return singleLineComment();
				}
				else if (getCurrentCharacter() == ASTERISK)
				{
					takeCurrent();
					return transition(()-> blockComment());
				}
				else if (getCurrentCharacter() == '=')
				{
					takeCurrent();
					return stay(endSymbol(JavaSymbolType.DivideAssign));
				}
				else
				{
					return stay(endSymbol(JavaSymbolType.Slash));
				}
			default:
				return stay(endSymbol(operator()));
		}
	}

	private StateResult atSymbol()
	{
		takeCurrent();
		if (getCurrentCharacter() == '"')
		{
			takeCurrent();
			return transition(()-> verbatimStringLiteral());
		}
		else if (getCurrentCharacter() == ASTERISK)
		{
			return transition(endSymbol(JavaSymbolType.RazorCommentTransition), ()-> afterRazorCommentTransition());
		}
		else if (getCurrentCharacter() == '@')
		{
			// Could be escaped comment transition

			return transition(endSymbol(JavaSymbolType.Transition), () ->
			{
				takeCurrent();
				return transition(endSymbol(JavaSymbolType.Transition), ()->Data());
			}
		   );
		}
		return stay(endSymbol(JavaSymbolType.Transition));
	}

	private JavaSymbolType operator()
	{
		char first = getCurrentCharacter();
		takeCurrent();
		Supplier<JavaSymbolType> handler = null;
		if ((handler = _operatorHandlers.get(first)) != null)
		{
			return handler.get();
		}
		return JavaSymbolType.Unknown;
	}

	private JavaSymbolType lessThanOperator()
	{
		if (getCurrentCharacter() == '=')
		{
			takeCurrent();
			return JavaSymbolType.LessThanEqual;
		}
		return JavaSymbolType.LessThan;
	}

	private JavaSymbolType greaterThanOperator()
	{
		if (getCurrentCharacter() == '=')
		{
			takeCurrent();
			return JavaSymbolType.GreaterThanEqual;
		}
		return JavaSymbolType.GreaterThan;
	}

	private JavaSymbolType minusOperator()
	{
		if (getCurrentCharacter() == '>')
		{
			takeCurrent();
			return JavaSymbolType.Arrow;
		}
		else if (getCurrentCharacter() == '-')
		{
			takeCurrent();
			return JavaSymbolType.Decrement;
		}
		else if (getCurrentCharacter() == '=')
		{
			takeCurrent();
			return JavaSymbolType.MinusAssign;
		}
		return JavaSymbolType.Minus;
	}

	private Supplier<JavaSymbolType> createTwoCharOperatorHandler(JavaSymbolType typeIfOnlyFirst, char second, JavaSymbolType typeIfBoth)
	{

		return () ->
		{
			if (getCurrentCharacter() == second)
			{
				takeCurrent();
				return typeIfBoth;
			}
			return typeIfOnlyFirst;
		};
	}

	private Supplier<JavaSymbolType> createTwoCharOperatorHandler(JavaSymbolType typeIfOnlyFirst, char option1, JavaSymbolType typeIfOption1, char option2, JavaSymbolType typeIfOption2)
	{

		return () ->
		{
			if (getCurrentCharacter() == option1)
			{
				takeCurrent();
				return typeIfOption1;
			}
			else if (getCurrentCharacter() == option2)
			{
				takeCurrent();
				return typeIfOption2;
			}
			return typeIfOnlyFirst;
		};
	}

	private StateResult verbatimStringLiteral()
	{

		takeUntil(c -> c == '"');
		if (getCurrentCharacter() == '"')
		{
			takeCurrent();
			if (getCurrentCharacter() == '"')
			{
				takeCurrent();
				// stay in the literal, this is an escaped "
				return stay();
			}
		}
		else if (getEndOfFile())
		{
			getCurrentErrors().add(new RazorError(RazorResources.getResource(RazorResources.ParseError_Unterminated_String_Literal), getCurrentStart().clone()));
		}
		return transition(endSymbol(JavaSymbolType.StringLiteral), ()->Data());
	}

	private StateResult quotedLiteral(char quote, JavaSymbolType literalType)
	{

		takeUntil(c -> c == BACKSLASH || c == quote || ParserHelpers.isNewLine(c));
		if (getCurrentCharacter() == BACKSLASH)
		{
			takeCurrent(); // Take the '\'

			// If the next char is the same quote that started this
			if (getCurrentCharacter() == quote || getCurrentCharacter() == BACKSLASH)
			{
				takeCurrent(); // Take it so that we don't prematurely end the literal.
			}
			return stay();
		}
		else if (getEndOfFile() || ParserHelpers.isNewLine(getCurrentCharacter()))
		{
			getCurrentErrors().add(new RazorError(RazorResources.getResource(RazorResources.ParseError_Unterminated_String_Literal), getCurrentStart().clone()));
		}
		else
		{
			takeCurrent(); // No-op if at EOF
		}
		return transition(endSymbol(literalType), ()->Data());
	}


	private StateResult blockComment()
	{

		takeUntil(c -> c == ASTERISK);
		if (getEndOfFile())
		{
			getCurrentErrors().add(new RazorError(RazorResources.getResource(RazorResources.ParseError_BlockComment_Not_Terminated), getCurrentStart().clone()));
			return transition(endSymbol(JavaSymbolType.Comment), ()->Data());
		}
		if (getCurrentCharacter() == ASTERISK)
		{
			takeCurrent();
			if (getCurrentCharacter() == '/')
			{
				takeCurrent();
				return transition(endSymbol(JavaSymbolType.Comment), ()->Data());
			}
		}
		return stay();
	}


	private StateResult singleLineComment()
	{

		takeUntil(c -> ParserHelpers.isNewLine(c));
		return stay(endSymbol(JavaSymbolType.Comment));
	}

	
	private StateResult numericLiteral()
	{
		if (takeAll("0x", true))
		{
			return hexLiteral();
		}
		else
		{
			return decimalLiteral();
		}
	}

	private StateResult hexLiteral()
	{

		takeUntil(c -> !ParserHelpers.isHexDigit(c));
		takeIntegerSuffix();
		return stay(endSymbol(JavaSymbolType.IntegerLiteral));
	}

	private StateResult decimalLiteral()
	{

		takeUntil(c -> !Character.isDigit(c));
		if (getCurrentCharacter() == '.' && Character.isDigit(peek()))
		{
			return realLiteral();
		}
		else if (JavaHelpers.isRealLiteralSuffix(getCurrentCharacter()) || getCurrentCharacter() == 'E' || getCurrentCharacter() == 'e')
		{
			return realLiteralExponentPart();
		}
		else
		{
			takeIntegerSuffix();
			return stay(endSymbol(JavaSymbolType.IntegerLiteral));
		}
	}

	private StateResult realLiteralExponentPart()
	{
		if (getCurrentCharacter() == 'E' || getCurrentCharacter() == 'e')
		{
			takeCurrent();
			if (getCurrentCharacter() == '+' || getCurrentCharacter() == '-')
			{
				takeCurrent();
			}

			takeUntil(c -> !Character.isDigit(c));
		}
		if (JavaHelpers.isRealLiteralSuffix(getCurrentCharacter()))
		{
			takeCurrent();
		}
		return stay(endSymbol(JavaSymbolType.RealLiteral));
	}


	private StateResult realLiteral()
	{
		assertCurrent('.');
		takeCurrent();
		assert Character.isDigit(getCurrentCharacter());

		takeUntil(c -> !Character.isDigit(c));
		return realLiteralExponentPart();
	}

	private void takeIntegerSuffix()
	{
		if (Character.toLowerCase(getCurrentCharacter()) == 'u')
		{
			takeCurrent();
			if (Character.toLowerCase(getCurrentCharacter()) == 'l')
			{
				takeCurrent();
			}
		}
		else if (Character.toLowerCase(getCurrentCharacter()) == 'l')
		{
			takeCurrent();
			if (Character.toLowerCase(getCurrentCharacter()) == 'u')
			{
				takeCurrent();
			}
		}
	}


	private StateResult identifier()
	{
		assert JavaHelpers.isIdentifierStart(getCurrentCharacter());
		takeCurrent();

		takeUntil(c -> !JavaHelpers.isIdentifierPart(c));
		JavaSymbol sym = null;
		if (getHaveContent())
		{
			String currentChars=getBuffer().toString();
			JavaKeyword kwd = JavaKeywordDetector.symbolTypeForIdentifier(currentChars);
			JavaSymbolType type = JavaSymbolType.Identifier;
			if (kwd != null)
			{
				type = JavaSymbolType.Keyword;
			}
			JavaSymbol tempVar=null;
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