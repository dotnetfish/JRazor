package com.superstudio.web.razor.parser;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.web.*;
import com.superstudio.web.razor.*;
import com.superstudio.web.razor.parser.syntaxTree.*;
import com.superstudio.web.razor.text.*;
import com.superstudio.web.razor.tokenizer.HtmlTokenizer;
import com.superstudio.web.razor.tokenizer.symbols.*;

public class HtmlLanguageCharacteristics extends LanguageCharacteristics<HtmlTokenizer, HtmlSymbol, HtmlSymbolType>
{
	private static final HtmlLanguageCharacteristics _instance = new HtmlLanguageCharacteristics();

	private HtmlLanguageCharacteristics()
	{
	}

	public static HtmlLanguageCharacteristics getInstance()
	{
		return _instance;
	}

	@Override
	public String getSample(HtmlSymbolType type)
	{
		switch (type)
		{
			case Text:
				return RazorResources.getHtmlSymbol_Text();
			case WhiteSpace:
				return RazorResources.getHtmlSymbol_WhiteSpace();
			case NewLine:
				return RazorResources.getHtmlSymbol_NewLine();
			case OpenAngle:
				return "<";
			case Bang:
				return "!";
			case Solidus:
				return "/";
			case QuestionMark:
				return "?";
			case DoubleHyphen:
				return "--";
			case LeftBracket:
				return "[";
			case CloseAngle:
				return ">";
			case RightBracket:
				return "]";
			case Equals:
				return "=";
			case DoubleQuote:
				return "\"";
			case SingleQuote:
				return "'";
			case Transition:
				return "@";
			case Colon:
				return ":";
			case RazorComment:
				return RazorResources.getHtmlSymbol_RazorComment();
			case RazorCommentStar:
				return "*";
			case RazorCommentTransition:
				return "@";
			default:
				return RazorResources.getSymbol_Unknown();
		}
	}

	@Override
	public HtmlTokenizer createTokenizer(ITextDocument source)
	{
		try {
			return new HtmlTokenizer(source);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public HtmlSymbolType flipBracket(HtmlSymbolType bracket)
	{
		switch (bracket)
		{
			case LeftBracket:
				return HtmlSymbolType.RightBracket;
			case OpenAngle:
				return HtmlSymbolType.CloseAngle;
			case RightBracket:
				return HtmlSymbolType.LeftBracket;
			case CloseAngle:
				return HtmlSymbolType.OpenAngle;
			default:
				//Debug.Fail("flipBracket must be called with a bracket character");
				return HtmlSymbolType.Unknown;
		}
	}

	@Override
	public HtmlSymbol createMarkerSymbol(SourceLocation location)
	{
		try {
			return new HtmlSymbol(location, "", HtmlSymbolType.Unknown);
		} catch (ArgumentNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public HtmlSymbolType getKnownSymbolType(KnownSymbolType type)
	{
		switch (type)
		{
			case CommentStart:
				return HtmlSymbolType.RazorCommentTransition;
			case CommentStar:
				return HtmlSymbolType.RazorCommentStar;
			case CommentBody:
				return HtmlSymbolType.RazorComment;
			case Identifier:
			case Keyword:
				return HtmlSymbolType.Text;
			case NewLine:
				return HtmlSymbolType.NewLine;
			case Transition:
				return HtmlSymbolType.Transition;
			case WhiteSpace:
				return HtmlSymbolType.WhiteSpace;
			default:
				return HtmlSymbolType.Unknown;
		}
	}

	@Override
	protected HtmlSymbol createSymbol(SourceLocation location, String content, HtmlSymbolType type, Iterable<RazorError> errors)
	{
		try {
			return new HtmlSymbol(location, content, type, errors);
		} catch (ArgumentNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean equals(Object obj, Object others) {
		// TODO Auto-generated method stub
		return obj.equals(others);
	}
}