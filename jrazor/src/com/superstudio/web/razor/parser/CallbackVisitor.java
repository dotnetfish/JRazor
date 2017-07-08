package com.superstudio.web.razor.parser;

import com.superstudio.commons.SynchronizationContext;
import com.superstudio.web.razor.parser.syntaxTree.Block;
import com.superstudio.web.razor.parser.syntaxTree.BlockType;
import com.superstudio.web.razor.parser.syntaxTree.RazorError;
import com.superstudio.web.razor.parser.syntaxTree.Span;

import java.util.function.Consumer;


public class CallbackVisitor extends ParserVisitor {
	private Consumer<Span> _spanCallback;
	private Consumer<RazorError> _errorCallback;
	private Consumer<BlockType> _endBlockCallback;
	private Consumer<BlockType> _startBlockCallback;
	private Runnable _completeCallback;

	public CallbackVisitor(Consumer<Span> spanCallback) {

		// methods are not converted
		this(spanCallback, (t) -> {
		});

	}

	public CallbackVisitor(Consumer<Span> spanCallback, Consumer<RazorError> errorCallback) {
		// TODO TASK: Lambda expressions and anonymous methods are not converted
		//
		this(spanCallback, errorCallback, (t) -> {
		} , (t) -> {
		});
		// {
	}

	public CallbackVisitor(Consumer<Span> spanCallback, Consumer<RazorError> errorCallback,
			Consumer<BlockType> startBlockCallback, Consumer<BlockType> endBlockCallback) {

		// methods are not converted
		this(spanCallback, errorCallback, startBlockCallback, endBlockCallback, () -> {
		});

	}

	public CallbackVisitor(Consumer<Span> spanCallback, Consumer<RazorError> errorCallback,
			Consumer<BlockType> startBlockCallback, Consumer<BlockType> endBlockCallback, Runnable completeCallback) {
		_spanCallback = spanCallback;
		_errorCallback = errorCallback;
		_startBlockCallback = startBlockCallback;
		_endBlockCallback = endBlockCallback;
		_completeCallback = completeCallback;
	}

	private SynchronizationContext synchronizationContext;

	/*public SynchronizationContext getSynchronizationContext() {
		return synchronizationContext;
	}
*/
	public void setSynchronizationContext(SynchronizationContext context) {
		synchronizationContext = context;
	}

	@Override
	public void visitStartBlock(Block block) throws Exception {
		super.visitStartBlock(block);
		raiseCallback(synchronizationContext, block.getType(), _startBlockCallback);
	}

	@Override
	public void visitSpan(Span span) throws Exception {
		super.visitSpan(span);
		raiseCallback(synchronizationContext, span, _spanCallback);
	}

	@Override
	public void visitEndBlock(Block block) throws Exception {
		super.visitEndBlock(block);
		raiseCallback(synchronizationContext, block.getType(), _endBlockCallback);
	}

	@Override
	public void visitError(RazorError err) throws Exception {
		super.visitError(err);
		raiseCallback(synchronizationContext, err, _errorCallback);
	}

	@Override
	public void onComplete() throws Exception {
		super.onComplete();

		// methods are not converted
		raiseCallback(synchronizationContext, null, (t) -> {
			_completeCallback.run();
		});
	}

	private <T> void raiseCallback(SynchronizationContext syncContext, T param, Consumer<T> callback) {
		if (callback != null) {
			if (syncContext != null) {

				// anonymous methods are not converted
				syncContext.Post(state -> callback.accept((T) state), param);
			} else {
				callback.accept(param);
			}
		}
	}
}
