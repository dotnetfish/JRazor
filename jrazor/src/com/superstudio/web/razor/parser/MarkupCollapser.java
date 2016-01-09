package com.superstudio.web.razor.parser;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.csharpbridge.action.ActionThree;
import com.superstudio.web.razor.generator.MarkupCodeGenerator;
import com.superstudio.web.razor.parser.syntaxTree.*;
import com.superstudio.web.razor.text.*;



public class MarkupCollapser extends MarkupRewriter
{
	public MarkupCollapser(ActionThree<SpanBuilder, SourceLocation, String> markupSpanFactory) throws Exception
	{
		super(markupSpanFactory);
	}

	@Override
	protected boolean canRewrite(Span span)
	{
		return span.getKind() == SpanKind.Markup && span.getCodeGenerator() instanceof MarkupCodeGenerator;
	}

	@Override
	protected SyntaxTreeNode rewriteSpan(BlockBuilder parent, Span span)
	{
		// Only rewrite if we have a previous that is also markup (canRewrite does this check for us!)
		//Object tempVar = parent.getChildren().LastOrDefault();
		Object tempVar=CollectionHelper.lastOrDefault(parent.getChildren());
		Span previous = (Span)((tempVar instanceof Span) ? tempVar : null);
		if (previous == null || !canRewrite(previous))
		{
			return span;
		}

		// Merge spans
		parent.getChildren().remove(previous);
		SpanBuilder merged = new SpanBuilder();
		FillSpan(merged, previous.getStart(), previous.getContent() + span.getContent());
		return merged.build();
	}

	@Override
	public Block rewrite(Block input) {
		// TODO Auto-generated method stub
		return null;
	}

	
}