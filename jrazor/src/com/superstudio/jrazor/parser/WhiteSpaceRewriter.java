package com.superstudio.jrazor.parser;

import com.superstudio.commons.csharpbridge.action.ActionThree;
import com.superstudio.jrazor.parser.syntaxTree.*;
import com.superstudio.jrazor.text.*;
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

	//public override void VisitBlock(Block block)
	//{
	//    BlockBuilder parent = null;
	//    if (_blocks.Count > 0)
	//    {
	//        parent = _blocks.Peek();
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
	//        base.VisitBlock(block);
	//    }
	//    if (_blocks.Count > 1)
	//    {
	//        parent.Children.Add(_blocks.Pop().Build());
	//    }
	//}

	//public override void VisitSpan(Span span)
	//{
	//    //Debug.Assert(_blocks.Count > 0);
	//    _blocks.Peek().Children.Add(span);
	//}

	@Override
	protected SyntaxTreeNode rewriteBlock(BlockBuilder parent, Block block)
	{
		BlockBuilder newBlock = new BlockBuilder(block);
		newBlock.getChildren().clear();
		Object tempVar = CollectionHelper.firstOrDefault(block.getChildren());//.FirstOrDefault();
		Span ws = (Span)((tempVar instanceof Span) ? tempVar : null);
		Iterable<SyntaxTreeNode> newNodes = block.getChildren();
		String content= ws.getContent();
		
		if (CollectionHelper.all(content.toCharArray(),(p)->Character.isWhitespace(p)))
		{
			// Add this node to the parent
			SpanBuilder builder = new SpanBuilder(ws);
			builder.clearSymbols();
			fillSpan(builder, ws.getStart(), ws.getContent());
			parent.getChildren().add(builder.build());

			// Remove the old whitespace node
			newNodes = CollectionHelper.skip(block.getChildren(), 1);
		}

		for (SyntaxTreeNode node : newNodes)
		{
			newBlock.getChildren().add(node);
		}
		return newBlock.build();
	}
}