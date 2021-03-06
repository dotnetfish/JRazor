﻿package com.superstudio.jrazor.parser;

import java.util.function.Consumer;

import com.superstudio.commons.SynchronizationContext;
import com.superstudio.commons.csharpbridge.action.Action;
import com.superstudio.commons.exception.OperationCanceledException;
import com.superstudio.jrazor.parser.syntaxTree.Block;
import com.superstudio.jrazor.parser.syntaxTree.BlockType;
import com.superstudio.jrazor.parser.syntaxTree.RazorError;
import com.superstudio.jrazor.parser.syntaxTree.Span;



public class CallbackVisitor extends ParserVisitor {
	private Consumer<Span> _spanCallback;
	private Consumer<RazorError> _errorCallback;
	private Consumer<BlockType> _endBlockCallback;
	private Consumer<BlockType> _startBlockCallback;
	private Action _completeCallback;

	public CallbackVisitor(Consumer<Span> spanCallback) {
		 
		 
		this(spanCallback, (t) -> {
		});

	}

	public CallbackVisitor(Consumer<Span> spanCallback, Consumer<RazorError> errorCallback) {
		// TODO TASK: Lambda expressions and anonymous methods are not converted
		// by C# to Java Converter:
		this(spanCallback, errorCallback, (t) -> {
		} , (t) -> {
		});
		// {
	}

	public CallbackVisitor(Consumer<Span> spanCallback, Consumer<RazorError> errorCallback,
			Consumer<BlockType> startBlockCallback, Consumer<BlockType> endBlockCallback) {
		 
		 
		this(spanCallback, errorCallback, startBlockCallback, endBlockCallback, () -> {
		});

	}

	public CallbackVisitor(Consumer<Span> spanCallback, Consumer<RazorError> errorCallback,
			Consumer<BlockType> startBlockCallback, Consumer<BlockType> endBlockCallback, Action completeCallback) {
		_spanCallback = spanCallback;
		_errorCallback = errorCallback;
		_startBlockCallback = startBlockCallback;
		_endBlockCallback = endBlockCallback;
		_completeCallback = completeCallback;
	}

	private SynchronizationContext synchronizationContext;

	public SynchronizationContext getSynchronizationContext() {
		return synchronizationContext;
	}

	public void setSynchronizationContext(SynchronizationContext context) {
		synchronizationContext = context;
	}

	@Override
	public void visitStartBlock(Block block) throws Exception {
		super.visitStartBlock(block);
		raiseCallback(synchronizationContext, block.getType(), _startBlockCallback);
	}

	@Override
	public void visitSpan(Span span) throws OperationCanceledException, Exception {
		super.visitSpan(span);
		raiseCallback(synchronizationContext, span, _spanCallback);
	}

	@Override
	public void visitEndBlock(Block block) throws OperationCanceledException, Exception {
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
		 
		 
		raiseCallback(synchronizationContext, null, (t) -> {
			_completeCallback.execute();
		});
	}

	private <T> void raiseCallback(SynchronizationContext syncContext, T param, Consumer<T> callback) {
		if (callback != null) {
			if (syncContext != null) {
				 
				 
				syncContext.Post(state -> callback.accept((T) state), param);
			} else {
				callback.accept(param);
			}
		}
	}
}
