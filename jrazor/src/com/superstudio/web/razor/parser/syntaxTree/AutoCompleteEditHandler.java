package com.superstudio.web.razor.parser.syntaxTree;


import java.util.function.Function;

import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.PartialParseResult;
import com.superstudio.web.razor.editor.SpanEditHandler;
import com.superstudio.web.razor.parser.ParserHelpers;
import com.superstudio.web.razor.text.TextChange;
import com.superstudio.web.razor.tokenizer.symbols.ISymbol;
import org.apache.commons.lang3.StringUtils;


public class AutoCompleteEditHandler extends SpanEditHandler
{
	public AutoCompleteEditHandler(Function<String, Iterable<? extends ISymbol>> tokenizer)
	{
		super(tokenizer);
	}

	public AutoCompleteEditHandler(Function<String, Iterable<? extends ISymbol>> tokenizer, AcceptedCharacters accepted)
	{
		super(tokenizer, accepted);
	}

	private boolean privateAutoCompleteAtEndOfSpan;
	public final boolean getAutoCompleteAtEndOfSpan()
	{
		return privateAutoCompleteAtEndOfSpan;
	}
	public final void setAutoCompleteAtEndOfSpan(boolean value)
	{
		privateAutoCompleteAtEndOfSpan = value;
	}
	private String privateAutoCompleteString;
	public final String getAutoCompleteString()
	{
		return privateAutoCompleteString;
	}
	public final void setAutoCompleteString(String value)
	{
		privateAutoCompleteString = value;
	}

	@Override
	protected PartialParseResult canAcceptChange(Span target, TextChange normalizedChange)
	{
		if (((getAutoCompleteAtEndOfSpan() && isAtEndOfSpan(target, normalizedChange)) || isAtEndOfFirstLine(target, normalizedChange)) && normalizedChange.getIsInsert() && ParserHelpers.isNewLine(normalizedChange.getNewText()) && getAutoCompleteString() != null)
		{
			PartialParseResult ret=PartialParseResult.forValue(PartialParseResult.Rejected.getValue() | PartialParseResult.AutoCompleteBlock.getValue());
			return ret;
		}
		return PartialParseResult.Rejected;
	}

	@Override
	public String toString()
	{
		String tempVar = getAutoCompleteString();
		return super.toString() + ",AutoComplete:[" + ((tempVar != null) ? tempVar : "<null>") + "]" + (getAutoCompleteAtEndOfSpan() ? ";AtEnd" : ";AtEOL");
	}

	@Override
	public boolean equals(Object obj)
	{
		AutoCompleteEditHandler other = (AutoCompleteEditHandler)((obj instanceof AutoCompleteEditHandler) ? obj : null);
		return super.equals(obj) && other != null && StringUtils.equals(other.getAutoCompleteString(), getAutoCompleteString()) && getAutoCompleteAtEndOfSpan() == other.getAutoCompleteAtEndOfSpan();
	}

	@Override
	public int hashCode()
	{
		return HashCodeCombiner.Start().Add(super.hashCode()).Add(getAutoCompleteString()).getCombinedHash();
	}
}