package com.superstudio.web.razor.parser;

import com.superstudio.commons.CancellationToken;
import com.superstudio.commons.exception.OperationCanceledException;
import com.superstudio.web.razor.ParserResults;
import com.superstudio.web.razor.parser.syntaxTree.*;



public abstract class ParserVisitor 
{
	private CancellationToken privateCancelToken;
	public final CancellationToken getCancelToken()
	{
		return privateCancelToken;
	}
	public final void setCancelToken(CancellationToken value)
	{
		privateCancelToken = value;
	}

	public void visitBlock(Block block) throws Exception
	{
		visitStartBlock(block);
		for (SyntaxTreeNode node : block.getChildren())
		{
			node.accept(this);
		}
		visitEndBlock(block);
	}

	public void visitStartBlock(Block block) throws Exception
	{
		throwIfCanceled();
	}

	public void visitSpan(Span span) throws Exception
	{
		throwIfCanceled();
	}

	public void visitEndBlock(Block block) throws Exception
	{
		throwIfCanceled();
	}

	public void visitError(RazorError err) throws Exception
	{
		throwIfCanceled();
	}

	public void onComplete() throws Exception
	{
		throwIfCanceled();
	}

	public void throwIfCanceled() throws Exception
	{
		if (getCancelToken() != null && getCancelToken().isCancellationRequested())
		{
			throw new OperationCanceledException();
		}
	}
	
	public  void visit(ParserResults result) throws Exception
	{
		
		if (result == null)
		{
			//throw new ArgumentNullException("result");
		}

		result.getDocument().accept(this);
		for (RazorError error : result.getParserErrors())
		{
			visitError(error);
		}
		onComplete();
	}
}