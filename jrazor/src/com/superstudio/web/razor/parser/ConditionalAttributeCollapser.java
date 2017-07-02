package com.superstudio.web.razor.parser;


import com.superstudio.commons.csharpbridge.action.ActionThree;
import com.superstudio.web.razor.editor.SpanEditHandler;
import com.superstudio.web.razor.generator.*;
import com.superstudio.web.razor.parser.syntaxTree.*;
import com.superstudio.web.razor.text.SourceLocation;
import com.superstudio.web.razor.tokenizer.HtmlTokenizer;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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
                && block.getChildren().stream().allMatch(i -> isLiteralAttributeValue(i));
    }

    protected SyntaxTreeNode rewriteBlock(BlockBuilder parent, Block block) {
        // Collect the content of this node
        List<String> contentList = block.getChildren().stream().map(i -> {
            return ((Span) i).getContent();
        }).collect(Collectors.toList());

        String contents = StringUtils.join(contentList, "");

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
        SourceLocation sourceLocation = block.getChildren().stream().findFirst().get().getStart();
        FillSpan(span, sourceLocation, contents);
        return span.build();

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