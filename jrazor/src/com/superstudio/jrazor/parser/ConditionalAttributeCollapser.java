package com.superstudio.jrazor.parser;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.csharpbridge.action.ActionThree;
import com.superstudio.jrazor.editor.SpanEditHandler;
import com.superstudio.jrazor.generator.AttributeBlockCodeGenerator;
import com.superstudio.jrazor.generator.IBlockCodeGenerator;
import com.superstudio.jrazor.generator.ISpanCodeGenerator;
import com.superstudio.jrazor.generator.LiteralAttributeCodeGenerator;
import com.superstudio.jrazor.generator.MarkupCodeGenerator;
import com.superstudio.jrazor.generator.SpanCodeGenerator;
import com.superstudio.jrazor.parser.syntaxTree.Block;
import com.superstudio.jrazor.parser.syntaxTree.BlockBuilder;
import com.superstudio.jrazor.parser.syntaxTree.Span;
import com.superstudio.jrazor.parser.syntaxTree.SpanBuilder;
import com.superstudio.jrazor.parser.syntaxTree.SyntaxTreeNode;
import com.superstudio.jrazor.text.SourceLocation;
import com.superstudio.jrazor.tokenizer.HtmlTokenizer;

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
		 
		 
		String content = StringHelper
				.concat(CollectionHelper.select(block.getChildren(), (p) -> ((Span) p).getContent()));// (block.getChildren().Select(s
																										// ->
																										// s.Content));

		// Create a new span containing this content
		SpanBuilder span = new SpanBuilder();
		span.setEditHandler(new SpanEditHandler((p) -> {
			try {
				return HtmlTokenizer.tokenize(p);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}));
		fillSpan(span, CollectionHelper.firstOrDefault(block.getChildren()).getStart(), content);
		return span.build();
		// string content = String.Concat(block.Children.Cast<Span>().Select(s
		// -> s.Content));

		// Create a new span containing this content
		/*
		 * SpanBuilder span = new SpanBuilder(); span.EditHandler = new
		 * SpanEditHandler(HtmlTokenizer.Tokenize); FillSpan(span,
		 * block.Children.Cast<Span>().First().Start, content); return
		 * span.Build();
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