package com.superstudio.language.java.parser;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.language.java.symbols.JavaKeyword;
import com.superstudio.language.java.symbols.JavaSymbol;
import com.superstudio.language.java.symbols.JavaSymbolType;
import com.superstudio.language.java.tokenizer.JavaTokenizer;
import com.superstudio.web.RazorResources;
import com.superstudio.web.razor.parser.LanguageCharacteristics;
import com.superstudio.web.razor.parser.syntaxTree.RazorError;
import com.superstudio.web.razor.text.ITextDocument;
import com.superstudio.web.razor.text.SourceLocation;
import com.superstudio.web.razor.tokenizer.symbols.KnownSymbolType;


public class JavaLanguageCharacteristics extends LanguageCharacteristics<JavaTokenizer, JavaSymbol, JavaSymbolType>
{
	private static final JavaLanguageCharacteristics _instance = new JavaLanguageCharacteristics();

	private static java.util.HashMap<JavaSymbolType, String> _symbolSamples = new java.util.HashMap<JavaSymbolType, String>();
	static
	{
		_symbolSamples.put(JavaSymbolType.Arrow, "->");
		_symbolSamples.put(JavaSymbolType.Minus, "-");
		_symbolSamples.put(JavaSymbolType.Decrement, "--");
		_symbolSamples.put(JavaSymbolType.MinusAssign, "-=");
		_symbolSamples.put(JavaSymbolType.NotEqual, "!=");
		_symbolSamples.put(JavaSymbolType.Not, "!");
		_symbolSamples.put(JavaSymbolType.Modulo, "%");
		_symbolSamples.put(JavaSymbolType.ModuloAssign, "%=");
		_symbolSamples.put(JavaSymbolType.AndAssign, "&=");
		_symbolSamples.put(JavaSymbolType.And, "&");
		_symbolSamples.put(JavaSymbolType.DoubleAnd, "&&");
		_symbolSamples.put(JavaSymbolType.LeftParenthesis, "(");
		_symbolSamples.put(JavaSymbolType.RightParenthesis, ")");
		_symbolSamples.put(JavaSymbolType.Star, "*");
		_symbolSamples.put(JavaSymbolType.MultiplyAssign, "*=");
		_symbolSamples.put(JavaSymbolType.Comma, ",");
		_symbolSamples.put(JavaSymbolType.Dot, ".");
		_symbolSamples.put(JavaSymbolType.Slash, "/");
		_symbolSamples.put(JavaSymbolType.DivideAssign, "/=");
		_symbolSamples.put(JavaSymbolType.DoubleColon, "::");
		_symbolSamples.put(JavaSymbolType.Colon, ":");
		_symbolSamples.put(JavaSymbolType.Semicolon, ";");
		_symbolSamples.put(JavaSymbolType.QuestionMark, "?");
		_symbolSamples.put(JavaSymbolType.NullCoalesce, "??");
		_symbolSamples.put(JavaSymbolType.RightBracket, "]");
		_symbolSamples.put(JavaSymbolType.LeftBracket, "[");
		_symbolSamples.put(JavaSymbolType.XorAssign, "^=");
		_symbolSamples.put(JavaSymbolType.Xor, "^");
		_symbolSamples.put(JavaSymbolType.LeftBrace, "{");
		_symbolSamples.put(JavaSymbolType.OrAssign, "|=");
		_symbolSamples.put(JavaSymbolType.DoubleOr, "||");
		_symbolSamples.put(JavaSymbolType.Or, "|");
		_symbolSamples.put(JavaSymbolType.RightBrace, "}");
		_symbolSamples.put(JavaSymbolType.Tilde, "~");
		_symbolSamples.put(JavaSymbolType.Plus, "+");
		_symbolSamples.put(JavaSymbolType.PlusAssign, "+=");
		_symbolSamples.put(JavaSymbolType.Increment, "++");
		_symbolSamples.put(JavaSymbolType.LessThan, "<");
		_symbolSamples.put(JavaSymbolType.LessThanEqual, "<=");
		_symbolSamples.put(JavaSymbolType.LeftShift, "<<");
		_symbolSamples.put(JavaSymbolType.LeftShiftAssign, "<<=");
		_symbolSamples.put(JavaSymbolType.Assign, "=");
		_symbolSamples.put(JavaSymbolType.Equals, "==");
		_symbolSamples.put(JavaSymbolType.GreaterThan, ">");
		_symbolSamples.put(JavaSymbolType.GreaterThanEqual, ">=");
		_symbolSamples.put(JavaSymbolType.RightShift, ">>");
		_symbolSamples.put(JavaSymbolType.RightShiftAssign, ">>>");
		_symbolSamples.put(JavaSymbolType.Hash, "#");
		_symbolSamples.put(JavaSymbolType.Transition, "@");
	}

	private JavaLanguageCharacteristics()
	{
	}

	public static JavaLanguageCharacteristics getInstance()
	{
		return _instance;
	}

	@Override
	public JavaTokenizer createTokenizer(ITextDocument source)
	{
		try {
			return new JavaTokenizer(source);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected JavaSymbol createSymbol(SourceLocation location, String content, JavaSymbolType type, Iterable<RazorError> errors)
	{
		try {
		
			return new JavaSymbol(location, content, type, errors);
		} catch (ArgumentNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getSample(JavaSymbolType type)
	{
		return getSymbolSample(type);
	}

	@Override
	public JavaSymbol createMarkerSymbol(SourceLocation location)
	{
		try {
			return new JavaSymbol(location, "", JavaSymbolType.Unknown);
		} catch (ArgumentNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JavaSymbolType getKnownSymbolType(KnownSymbolType type)
	{
		switch (type)
		{
			case Identifier:
				return JavaSymbolType.Identifier;
			case Keyword:
				return JavaSymbolType.Keyword;
			case NewLine:
				return JavaSymbolType.NewLine;
			case WhiteSpace:
				return JavaSymbolType.WhiteSpace;
			case Transition:
				return JavaSymbolType.Transition;
			case CommentStart:
				return JavaSymbolType.RazorCommentTransition;
			case CommentStar:
				return JavaSymbolType.RazorCommentStar;
			case CommentBody:
				return JavaSymbolType.RazorComment;
			default:
				return JavaSymbolType.Unknown;
		}
	}

	@Override
	public JavaSymbolType flipBracket(JavaSymbolType bracket)
	{
		switch (bracket)
		{
			case LeftBrace:
				return JavaSymbolType.RightBrace;
			case LeftBracket:
				return JavaSymbolType.RightBracket;
			case LeftParenthesis:
				return JavaSymbolType.RightParenthesis;
			case LessThan:
				return JavaSymbolType.GreaterThan;
			case RightBrace:
				return JavaSymbolType.LeftBrace;
			case RightBracket:
				return JavaSymbolType.LeftBracket;
			case RightParenthesis:
				return JavaSymbolType.LeftParenthesis;
			case GreaterThan:
				return JavaSymbolType.LessThan;
			default:
				//Debug.Fail("flipBracket must be called with a bracket character");
				return JavaSymbolType.Unknown;
		}
	}


	public static String getKeyword(JavaKeyword keyword)
	{
		return keyword.toString().toLowerCase();
	}

	public static String getSymbolSample(JavaSymbolType type)
	{
		String sample = null;
		if (!((sample = _symbolSamples.get(type)) != null))
		{
			switch (type)
			{
				case Identifier:
					return RazorResources.getJavaSymbol_Identifier();
				case Keyword:
					return RazorResources.getJavaSymbol_Keyword();
				case IntegerLiteral:
					return RazorResources.getJavaSymbol_IntegerLiteral();
				case NewLine:
					return RazorResources.getJavaSymbol_Newline();
				case WhiteSpace:
					return RazorResources.getJavaSymbol_Whitespace();
				case Comment:
					return RazorResources.getJavaSymbol_Comment();
				case RealLiteral:
					return RazorResources.getJavaSymbol_RealLiteral();
				case CharacterLiteral:
					return RazorResources.getJavaSymbol_CharacterLiteral();
				case StringLiteral:
					return RazorResources.getJavaSymbol_StringLiteral();
				default:
					return RazorResources.getSymbol_Unknown();
			}
		}
		return sample;
	}

	@Override
	public boolean equals(Object obj, Object others) {
		// TODO Auto-generated method stub
		return obj.equals(others);
	}

	
}