package com.superstudio.web.razor.parser;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.csharpbridge.action.ActionThree;
import com.superstudio.web.razor.parser.syntaxTree.*;
import com.superstudio.web.razor.text.*;
import com.superstudio.commons.CollectionHelper;




public class WhiteSpaceRewriter extends MarkupRewriter
{
	public WhiteSpaceRewriter(ActionThree<SpanBuilder, SourceLocation, String> markupSpanFactory) throws Exception
	{
		super(markupSpanFactory);
	}

	@Override
	protected boolean canRewrite(Block block)
	{
		return block.getType() == BlockType.Expression && getParent() != null;
	}

	//public override void visitBlock(Block block)
	//{
	//    BlockBuilder parent = null;
	//    if (_blocks.Count > 0)
	//    {
	//        parent = _blocks.peek();
	//    }
	//    BlockBuilder newBlock = new BlockBuilder(block);
	//    newBlock.Children.Clear();
	//    _blocks.Push(newBlock);
	//    if (block.getType() == BlockType.Expression && parent != null)
	//    {
	//        VisitExpressionBlock(block, parent);
	//    }
	//    else
	//    {
	//        base.visitBlock(block);
	//    }
	//    if (_blocks.Count > 1)
	//    {
	//        parent.Children.add(_blocks.Pop().build());
	//    }
	//}

	//public override void visitSpan(Span span)
	//{
	//    //Debug.Assert(_blocks.Count > 0);
	//    _blocks.peek().Children.add(span);
	//}

	@Override
	protected SyntaxTreeNode rewriteBlock(BlockBuilder parent, Block block)
	{
		BlockBuilder newBlock = new BlockBuilder(block);
		newBlock.getChildren().clear();

		//Object tempVar = CollectionHelper.firstOrDefault(block.getChildren());//.FirstOrDefault();
		Object firstChild=block.getChildren().stream().findFirst().get();
		Span ws = (Span)((firstChild instanceof Span) ? firstChild : null);
		Iterable<SyntaxTreeNode> newNodes = block.getChildren();
		String content= ws.getContent();


		if (CollectionHelper.all(content.toCharArray(),(p)->Character.isWhitespace(p)))
		//if(Stream.of(content.toCharArray()).anyMatch((p)->Character.isWhitespace(p)))
		{
			// add this node to the parent
			SpanBuilder builder = new SpanBuilder(ws);
			builder.clearSymbols();
			FillSpan(builder, ws.getStart(), ws.getContent());
			parent.getChildren().add(builder.build());

			// remove the old whitespace node
			newNodes = CollectionHelper.skip(block.getChildren(), 1);
		}

		for (SyntaxTreeNode node : newNodes)
		{
			newBlock.getChildren().add(node);
		}
		return newBlock.build();
	}
}