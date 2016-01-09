package com.superstudio.web.razor.parser;

import com.superstudio.commons.csharpbridge.action.ActionThree;
import com.superstudio.web.razor.editor.*;
import com.superstudio.web.razor.generator.*;
import com.superstudio.web.razor.parser.syntaxTree.*;
import com.superstudio.web.razor.text.*;
import com.superstudio.web.razor.tokenizer.*;
import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.csharpbridge.StringHelper;

public class ConditionalAttributeCollapser extends MarkupRewriter {
	public ConditionalAttributeCollapser(ActionThree<SpanBuilder, SourceLocation, String> markupSpanFactory)
			throws Exception {
		super(markupSpanFactory);
	}

	protected boolean canRewrite(Block block) {
		IBlockCodeGenerator tempVar = block.getCodeGenerator();
		AttributeBlockCodeGenerator gen = (AttributeBlockCodeGenerator) ((tempVar instanceof AttributeBlockCodeGenerator)
				? tempVar : null);
		return gen != null && !block.getChildren().isEmpty()
				&& CollectionHelper.all(block.getChildren(), (p) -> isLiteralAttributeValue(p));
	}

	protected SyntaxTreeNode rewriteBlock(BlockBuilder parent, Block block) {
		// Collect the content of this node

		// methods are not converted
		String content = StringHelper
				.concat(CollectionHelper.select(block.getChildren(), (p) -> ((Span) p).getContent()));// (block.getChildren().Select(s
																										// ->
																										// s.Content));

		// create a new span containing this content
		SpanBuilder span = new SpanBuilder();
		span.setEditHandler(new SpanEditHandler((p) -> {
			try {
				return HtmlTokenizer.tokenize(p);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}));
		FillSpan(span, CollectionHelper.firstOrDefault(block.getChildren()).getStart(), content);
		return span.build();
		// string content = String.Concat(block.Children.Cast<Span>().Select(s
		// -> s.Content));

		// create a new span containing this content
		/*
		 * SpanBuilder span = new SpanBuilder(); span.EditHandler = new
		 * SpanEditHandler(HtmlTokenizer.tokenize); FillSpan(span,
		 * block.Children.Cast<Span>().First().start, content); return
		 * span.build();
		 */
	}

	private boolean isLiteralAttributeValue(SyntaxTreeNode node) {
		if (node.getIsBlock()) {
			return false;
		}
		Span span = (Span) ((node instanceof Span) ? node : null);
		assert span != null;

		ISpanCodeGenerator tempVar = span.getCodeGenerator();
		LiteralAttributeCodeGenerator litGen = (LiteralAttributeCodeGenerator) ((tempVar instanceof LiteralAttributeCodeGenerator)
				? tempVar : null);

		return span != null && ((litGen != null && litGen.getValueGenerator() == null)
				|| span.getCodeGenerator() == SpanCodeGenerator.Null
				|| span.getCodeGenerator() instanceof MarkupCodeGenerator);
	}
}