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

/**
 * @author zj-db0720
 */
public class ConditionalAttributeCollapser extends MarkupRewriter {
    public ConditionalAttributeCollapser(ActionThree<SpanBuilder, SourceLocation, String> markupSpanFactory)
            throws Exception {
        super(markupSpanFactory);
    }

    @Override
    protected boolean canRewrite(Block block) {
        IBlockCodeGenerator codeGenerator = block.getCodeGenerator();

       return codeGenerator instanceof  AttributeBlockCodeGenerator  && !block.getChildren().isEmpty()
                && block.getChildren().stream().allMatch(i -> isLiteralAttributeValue(i));


    }

    @Override
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
        fillSpan(span, sourceLocation, contents);
        return span.build();

    }

    private boolean isLiteralAttributeValue(SyntaxTreeNode node) {
        if (node.getIsBlock()) {
            return false;
        }



        assert node instanceof  Span;
        Span span = (Span) node;

        ISpanCodeGenerator codeGenerator = span.getCodeGenerator();
        LiteralAttributeCodeGenerator litGen = (LiteralAttributeCodeGenerator) ((codeGenerator instanceof LiteralAttributeCodeGenerator)
                ? codeGenerator : null);

        return codeGenerator instanceof LiteralAttributeCodeGenerator && ((codeGenerator instanceof LiteralAttributeCodeGenerator && litGen.getValueGenerator() == null)
                || span.getCodeGenerator() == SpanCodeGenerator.Null
                || span.getCodeGenerator() instanceof MarkupCodeGenerator);
    }
}