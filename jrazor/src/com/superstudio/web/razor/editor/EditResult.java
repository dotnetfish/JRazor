package com.superstudio.web.razor.editor;

import com.superstudio.web.razor.*;
import com.superstudio.web.razor.parser.syntaxTree.*;



public class EditResult
{
	public EditResult(PartialParseResult result, SpanBuilder editedSpan)
	{
		setResult(result);
		setEditedSpan(editedSpan);
	}

	private PartialParseResult privateResult = PartialParseResult.forValue(0);
	public final PartialParseResult getResult()
	{
		return privateResult;
	}
	public final void setResult(PartialParseResult value)
	{
		privateResult = value;
	}
	private SpanBuilder privateEditedSpan;
	public final SpanBuilder getEditedSpan()
	{
		return privateEditedSpan;
	}
	public final void setEditedSpan(SpanBuilder value)
	{
		privateEditedSpan = value;
	}
}