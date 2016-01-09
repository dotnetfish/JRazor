package com.superstudio.web.razor.parser;

import com.superstudio.commons.csharpbridge.action.ActionThree;
import com.superstudio.web.razor.parser.syntaxTree.Block;
import com.superstudio.web.razor.parser.syntaxTree.BlockBuilder;
import com.superstudio.web.razor.parser.syntaxTree.Span;
import com.superstudio.web.razor.parser.syntaxTree.SpanBuilder;
import com.superstudio.web.razor.parser.syntaxTree.SyntaxTreeNode;
import com.superstudio.web.razor.text.SourceLocation;



public abstract class MarkupRewriter extends ParserVisitor implements ISyntaxTreeRewriter
{
	private java.util.Stack<BlockBuilder> _blocks = new java.util.Stack<BlockBuilder>();
	private ActionThree<SpanBuilder, SourceLocation, String> _markupSpanFactory;

	protected MarkupRewriter(ActionThree<SpanBuilder, SourceLocation, String> markupSpanFactory) throws Exception
	{
		if (markupSpanFactory == null)
		{
			//throw new ArgumentNullException("markupSpanFactory");
		}
		_markupSpanFactory = markupSpanFactory;
	}

	protected final BlockBuilder getParent()
	{
		return _blocks.size() > 0 ? _blocks.peek() : null;
	}

	@Override
	public Block rewrite(Block input) throws Exception
	{
		input.accept(this);
		assert _blocks.size() == 1;
		return _blocks.pop().build();
	}

	@Override
	public void visitBlock(Block block) throws Exception
	{
		if (canRewrite(block))
		{
			SyntaxTreeNode newNode = rewriteBlock(_blocks.peek(), block);
			if (newNode != null)
			{
				_blocks.peek().getChildren().add(newNode);
			}
		}
		else
		{
			// Not rewritable.
			BlockBuilder builder = new BlockBuilder(block);
			builder.getChildren().clear();
			_blocks.push(builder);
			super.visitBlock(block);
			//assert ReferenceEquals(builder, _blocks.peek());

			if (_blocks.size() > 1)
			{
				_blocks.pop();
				_blocks.peek().getChildren().add(builder.build());
			}
		}
	}

	@Override
	public void visitSpan(Span span)
	{
		if (canRewrite(span))
		{
			SyntaxTreeNode newNode = rewriteSpan(_blocks.peek(), span);
			if (newNode != null)
			{
				_blocks.peek().getChildren().add(newNode);
			}
		}
		else
		{
			_blocks.peek().getChildren().add(span);
		}
	}

	protected boolean canRewrite(Block block)
	{
		return false;
	}

	protected boolean canRewrite(Span span)
	{
		return false;
	}

	protected SyntaxTreeNode rewriteBlock(BlockBuilder parent, Block block)
	{
		return null;
		//throw new NotImplementedException();
	}

	protected SyntaxTreeNode rewriteSpan(BlockBuilder parent, Span span)
	{
		return null;
		//throw new NotImplementedException();
	}

	protected final void FillSpan(SpanBuilder builder, SourceLocation start, String content)
	{
		_markupSpanFactory.execute(builder, start, content);
	}
}