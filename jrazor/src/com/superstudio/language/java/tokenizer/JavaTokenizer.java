package com.superstudio.language.java.tokenizer;

import com.superstudio.commons.csharpbridge.action.Func;
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




public class JavaTokenizer extends Tokenizer<JavaSymbol, JavaSymbolType>
{
	private java.util.HashMap<Character, Func<JavaSymbolType>> _operatorHandlers;

	public JavaTokenizer(ITextDocument source) throws Exception
	{
		super(source);
		setCurrentState(()->Data());

		_operatorHandlers = new java.util.HashMap<Character, Func<JavaSymbolType>>();
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
			TakeCurrent();
			if (checkTwoCharNewline && getCurrentCharacter() == '\n')
			{
				TakeCurrent();
			}
			return stay(EndSymbol(JavaSymbolType.NewLine));
		}
		else if (ParserHelpers.isWhitespace(getCurrentCharacter()))
		{
			// CSharp Spec §2.3.3

			TakeUntil(c -> !ParserHelpers.isWhitespace(c));
			return stay(EndSymbol(JavaSymbolType.WhiteSpace));
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
				TakeCurrent();

				return transition(() -> quotedLiteral('\'', JavaSymbolType.CharacterLiteral));
			case '"':
				TakeCurrent();

				return transition(() -> quotedLiteral('"', JavaSymbolType.StringLiteral));
			case '.':
				if (Character.isDigit(Peek()))
				{
					return realLiteral();
				}
				return stay(Single(JavaSymbolType.Dot));
			case '/':
				TakeCurrent();
				if (getCurrentCharacter() == '/')
				{
					TakeCurrent();
					return singleLineComment();
				}
				else if (getCurrentCharacter() == '*')
				{
					TakeCurrent();
					return transition(()-> blockComment());
				}
				else if (getCurrentCharacter() == '=')
				{
					TakeCurrent();
					return stay(EndSymbol(JavaSymbolType.DivideAssign));
				}
				else
				{
					return stay(EndSymbol(JavaSymbolType.Slash));
				}
			default:
				return stay(EndSymbol(operator()));
		}
	}

	private StateResult atSymbol()
	{
		TakeCurrent();
		if (getCurrentCharacter() == '"')
		{
			TakeCurrent();
			return transition(()-> verbatimStringLiteral());
		}
		else if (getCurrentCharacter() == '*')
		{
			return transition(EndSymbol(JavaSymbolType.RazorCommentTransition), ()->AfterRazorCommentTransition());
		}
		else if (getCurrentCharacter() == '@')
		{
			// Could be escaped comment transition

			return transition(EndSymbol(JavaSymbolType.Transition), () ->
			{
				TakeCurrent();
				return transition(EndSymbol(JavaSymbolType.Transition), ()->Data());
			}
		   );
		}
		return stay(EndSymbol(JavaSymbolType.Transition));
	}

	private JavaSymbolType operator()
	{
		char first = getCurrentCharacter();
		TakeCurrent();
		Func<JavaSymbolType> handler = null;
		if ((handler = _operatorHandlers.get(first)) != null)
		{
			return handler.execute();
		}
		return JavaSymbolType.Unknown;
	}

	private JavaSymbolType lessThanOperator()
	{
		if (getCurrentCharacter() == '=')
		{
			TakeCurrent();
			return JavaSymbolType.LessThanEqual;
		}
		return JavaSymbolType.LessThan;
	}

	private JavaSymbolType greaterThanOperator()
	{
		if (getCurrentCharacter() == '=')
		{
			TakeCurrent();
			return JavaSymbolType.GreaterThanEqual;
		}
		return JavaSymbolType.GreaterThan;
	}

	private JavaSymbolType minusOperator()
	{
		if (getCurrentCharacter() == '>')
		{
			TakeCurrent();
			return JavaSymbolType.Arrow;
		}
		else if (getCurrentCharacter() == '-')
		{
			TakeCurrent();
			return JavaSymbolType.Decrement;
		}
		else if (getCurrentCharacter() == '=')
		{
			TakeCurrent();
			return JavaSymbolType.MinusAssign;
		}
		return JavaSymbolType.Minus;
	}

	private Func<JavaSymbolType> createTwoCharOperatorHandler(JavaSymbolType typeIfOnlyFirst, char second, JavaSymbolType typeIfBoth)
	{

		return () ->
		{
			if (getCurrentCharacter() == second)
			{
				TakeCurrent();
				return typeIfBoth;
			}
			return typeIfOnlyFirst;
		};
	}

	private Func<JavaSymbolType> createTwoCharOperatorHandler(JavaSymbolType typeIfOnlyFirst, char option1, JavaSymbolType typeIfOption1, char option2, JavaSymbolType typeIfOption2)
	{

		return () ->
		{
			if (getCurrentCharacter() == option1)
			{
				TakeCurrent();
				return typeIfOption1;
			}
			else if (getCurrentCharacter() == option2)
			{
				TakeCurrent();
				return typeIfOption2;
			}
			return typeIfOnlyFirst;
		};
	}

	private StateResult verbatimStringLiteral()
	{

		TakeUntil(c -> c == '"');
		if (getCurrentCharacter() == '"')
		{
			TakeCurrent();
			if (getCurrentCharacter() == '"')
			{
				TakeCurrent();
				// stay in the literal, this is an escaped "
				return stay();
			}
		}
		else if (getEndOfFile())
		{
			getCurrentErrors().add(new RazorError(RazorResources.getParseError_Unterminated_String_Literal(), getCurrentStart().clone()));
		}
		return transition(EndSymbol(JavaSymbolType.StringLiteral), ()->Data());
	}

	private StateResult quotedLiteral(char quote, JavaSymbolType literalType)
	{

		TakeUntil(c -> c == '\\' || c == quote || ParserHelpers.isNewLine(c));
		if (getCurrentCharacter() == '\\')
		{
			TakeCurrent(); // Take the '\'

			// If the next char is the same quote that started this
			if (getCurrentCharacter() == quote || getCurrentCharacter() == '\\')
			{
				TakeCurrent(); // Take it so that we don't prematurely end the literal.
			}
			return stay();
		}
		else if (getEndOfFile() || ParserHelpers.isNewLine(getCurrentCharacter()))
		{
			getCurrentErrors().add(new RazorError(RazorResources.getParseError_Unterminated_String_Literal(), getCurrentStart().clone()));
		}
		else
		{
			TakeCurrent(); // No-op if at EOF
		}
		return transition(EndSymbol(literalType), ()->Data());
	}

	// CSharp Spec §2.3.2
	private StateResult blockComment()
	{

		TakeUntil(c -> c == '*');
		if (getEndOfFile())
		{
			getCurrentErrors().add(new RazorError(RazorResources.getParseError_BlockComment_Not_Terminated(), getCurrentStart().clone()));
			return transition(EndSymbol(JavaSymbolType.Comment), ()->Data());
		}
		if (getCurrentCharacter() == '*')
		{
			TakeCurrent();
			if (getCurrentCharacter() == '/')
			{
				TakeCurrent();
				return transition(EndSymbol(JavaSymbolType.Comment), ()->Data());
			}
		}
		return stay();
	}

	// CSharp Spec §2.3.2
	private StateResult singleLineComment()
	{

		TakeUntil(c -> ParserHelpers.isNewLine(c));
		return stay(EndSymbol(JavaSymbolType.Comment));
	}

	// CSharp Spec §2.4.4
	private StateResult numericLiteral()
	{
		if (TakeAll("0x", true))
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

		TakeUntil(c -> !ParserHelpers.isHexDigit(c));
		takeIntegerSuffix();
		return stay(EndSymbol(JavaSymbolType.IntegerLiteral));
	}

	private StateResult decimalLiteral()
	{

		TakeUntil(c -> !Character.isDigit(c));
		if (getCurrentCharacter() == '.' && Character.isDigit(Peek()))
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
			return stay(EndSymbol(JavaSymbolType.IntegerLiteral));
		}
	}

	private StateResult realLiteralExponentPart()
	{
		if (getCurrentCharacter() == 'E' || getCurrentCharacter() == 'e')
		{
			TakeCurrent();
			if (getCurrentCharacter() == '+' || getCurrentCharacter() == '-')
			{
				TakeCurrent();
			}

			TakeUntil(c -> !Character.isDigit(c));
		}
		if (JavaHelpers.isRealLiteralSuffix(getCurrentCharacter()))
		{
			TakeCurrent();
		}
		return stay(EndSymbol(JavaSymbolType.RealLiteral));
	}


	private StateResult realLiteral()
	{
		AssertCurrent('.');
		TakeCurrent();
		assert Character.isDigit(getCurrentCharacter());

		TakeUntil(c -> !Character.isDigit(c));
		return realLiteralExponentPart();
	}

	private void takeIntegerSuffix()
	{
		if (Character.toLowerCase(getCurrentCharacter()) == 'u')
		{
			TakeCurrent();
			if (Character.toLowerCase(getCurrentCharacter()) == 'l')
			{
				TakeCurrent();
			}
		}
		else if (Character.toLowerCase(getCurrentCharacter()) == 'l')
		{
			TakeCurrent();
			if (Character.toLowerCase(getCurrentCharacter()) == 'u')
			{
				TakeCurrent();
			}
		}
	}


	private StateResult identifier()
	{
		assert JavaHelpers.isIdentifierStart(getCurrentCharacter());
		TakeCurrent();

		TakeUntil(c -> !JavaHelpers.isIdentifierPart(c));
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
		StartSymbol();
		return stay(sym);
	}
}