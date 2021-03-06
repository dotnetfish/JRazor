﻿package com.superstudio.jrazor.editor;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.superstudio.commons.CancellationToken;
import com.superstudio.commons.CancellationTokenSource;
import com.superstudio.commons.IDisposable;
import com.superstudio.commons.ManualResetEventSlim;
import com.superstudio.commons.Path;
import com.superstudio.commons.csharpbridge.action.ActionTwo;
import com.superstudio.jrazor.DocumentParseCompleteEventArgs;
import com.superstudio.jrazor.GeneratorResults;
import com.superstudio.jrazor.RazorEngineHost;
import com.superstudio.jrazor.RazorTemplateEngine;
import com.superstudio.jrazor.parser.syntaxTree.Block;
import com.superstudio.jrazor.parser.syntaxTree.Span;
import com.superstudio.jrazor.resources.RazorResources;
import com.superstudio.jrazor.text.ITextBuffer;
import com.superstudio.jrazor.text.TextChange;
import com.superstudio.jrazor.utils.DisposableAction;


public class BackgroundParser implements IDisposable {
	private MainThreadState _main;
	private BackgroundThread _bg;

	public BackgroundParser(RazorEngineHost host, String fileName) {
		_main = new MainThreadState(fileName);
		_bg = new BackgroundThread(_main, host, fileName);

		
		_main.setResultsReady((sender,args) -> onResultsReady(args));
	}

	/**
	 * Fired on the main thread.
	 * 
	 */
	// C# TO JAVA CONVERTER TODO TASK: Events are not available in Java:
	private ActionTwo<Object,DocumentParseCompleteEventArgs> resultsReady;

	public final boolean getIsIdle() {
		return _main.getIsIdle();
	}

	public final void start() {
		_bg.start();
	}

	public final void cancel() {
		_main.cancel();
	}

	public final void queueChange(TextChange change) {
		_main.queueChange(change);
	}

	 
	 
	// [SuppressMessage("Microsoft.Usage",
	// "CA2213:DisposableFieldsShouldBeDisposed", MessageId = "_main",
	// Justification = "MainThreadState is disposed when the background thread
	// shuts down")]
	public final void dispose() {
		_main.cancel();
	}

	public final IDisposable synchronizeMainThreadState() {
		return _main.lock();
	}

	protected void onResultsReady(DocumentParseCompleteEventArgs args) {
		ActionTwo<Object,DocumentParseCompleteEventArgs> handler = getResultsReady();
		if (handler != null) {
			handler.execute(this, args);
		}
	}

	public static boolean treesAreDifferent(Block leftTree, Block rightTree, Iterable<TextChange> changes) {
		return treesAreDifferent(leftTree, rightTree, changes, CancellationToken.None);
	}

	public static boolean treesAreDifferent(Block leftTree, Block rightTree, Iterable<TextChange> changes,
			CancellationToken cancelToken) {
		// Apply all the pending changes to the original tree
		// PERF: If this becomes a bottleneck, we can probably do it the other
		// way around,
		// i.e. visit the tree and find applicable changes for each node.
		for (TextChange change : changes) {
			cancelToken.ThrowIfCancellationRequested();
			Span changeOwner = leftTree.locateOwner(change);

			// Apply the change to the tree
			if (changeOwner == null) {
				return true;
			}
			EditResult result = changeOwner.getEditHandler().applyChange(changeOwner, change, true);
			changeOwner.replaceWith(result.getEditedSpan());
		}

		// Now compare the trees
		boolean treesDifferent = !leftTree.equivalentTo(rightTree);
		return treesDifferent;
	}

	public ActionTwo<Object,DocumentParseCompleteEventArgs> getResultsReady() {
		return resultsReady;
	}

	public void setResultsReady(ActionTwo<Object,DocumentParseCompleteEventArgs> resultsReady) {
		this.resultsReady = resultsReady;
	}

	private abstract static class ThreadStateBase {
		// #endif
		protected ThreadStateBase() {
		}

		 
			protected final void setThreadId(long l) {
		}

		 
		
			protected final void ensureOnThread() {
			 
			// #if //Debug
			////Debug.Assert(_id != -1, "SetThreadId was never called!");
			////Debug.Assert(Thread.currentThread().ManagedThreadId == _id, "Called from an unexpected thread!");
			// #endif
		}

		 
		// to .NET attributes:
		// [SuppressMessage("Microsoft.Performance",
		// "CA1822:MarkMembersAsStatic", Justification = "This method is only
		// empty in Release builds. In //Debug builds it contains references to
		// instance variables"), Conditional("//Debug")]
		protected final void ensureNotOnThread() {
			 
			// #if //Debug
			////Debug.Assert(_id != -1, "SetThreadId was never called!");
			////Debug.Assert(Thread.currentThread().ManagedThreadId != _id, "Called from an unexpected thread!");
			// #endif
		}
	}

	private  class MainThreadState extends ThreadStateBase implements IDisposable {
		private CancellationTokenSource _cancelSource = new CancellationTokenSource();
		private ManualResetEventSlim _hasParcel = new ManualResetEventSlim(false);
		private CancellationTokenSource _currentParcelCancelSource;
		
		private  ActionTwo<Object,DocumentParseCompleteEventArgs> resultsReady;
		 
		// to .NET attributes:
		// [SuppressMessage("Microsoft.Performance",
		// "CA1823:AvoidUnusedPrivateFields", Justification = "Field is used in
		// //Debug code and may be used later")]
		private String _fileName;
		private Object _stateLock = new Object();
		private java.util.List<TextChange> _changes = new java.util.ArrayList<TextChange>();

		public MainThreadState(String fileName) {
			_fileName = fileName;

			setThreadId((int) Thread.currentThread().getId());
		}

		public final CancellationToken getCancelToken() {
			return _cancelSource.getToken();
		}

		public final boolean getIsIdle() {
			synchronized (_stateLock) {
				return _currentParcelCancelSource == null;
			}
		}

		public final void cancel() {
			ensureOnThread();
			_cancelSource.Cancel();
		}

		public final IDisposable lock() {
			// Monitor.Enter(_stateLock);
			ReentrantLock lock = new ReentrantLock();
			lock.lock();
			 
			 
			return new DisposableAction(() -> lock.unlock());
		}

		public final void queueChange(TextChange change) {
			RazorEditorTrace.traceLine(RazorResources.getTrace_QueuingParse(), Path.GetFileName(_fileName), change);
			ensureOnThread();
			synchronized (_stateLock) {
				// CurrentParcel token source is not null =-> There's a parse
				// underway
				if (_currentParcelCancelSource != null) {
					_currentParcelCancelSource.Cancel();
				}

				_changes.add(change);
				_hasParcel.Set();
			}
		}

		public final WorkParcel getParcel() {
			ensureNotOnThread(); // Only the background thread can get a parcel
			_hasParcel.Wait(_cancelSource.getToken());
			_hasParcel.reset();
			synchronized (_stateLock) {
				// Create a cancellation source for this parcel
				_currentParcelCancelSource = new CancellationTokenSource();

				java.util.List<TextChange> changes = _changes;
				_changes = new java.util.ArrayList<TextChange>();
				return new WorkParcel(changes, _currentParcelCancelSource.getToken());
			}
		}

		public final void returnParcel(DocumentParseCompleteEventArgs args) {
			synchronized (_stateLock) {
				// Clear the current parcel cancellation source
				if (_currentParcelCancelSource != null) {
					_currentParcelCancelSource.dispose();
					_currentParcelCancelSource = null;
				}

				// If there are things waiting to be parsed, just don't fire the
				// event because we're already out of date
				if (!_changes.isEmpty()) {
					return;
				}
			}
			ActionTwo<Object,DocumentParseCompleteEventArgs> handler = getResultsReady();
			if (handler != null) {
				handler.execute(this,args);
			}
		}

		@Override
		public final void dispose() {
			dispose(true);
			System.gc();
			// GC.SuppressFinalize(this);
		}

		protected void dispose(boolean disposing) {
			if (disposing) {
				if (_currentParcelCancelSource != null) {
					_currentParcelCancelSource.dispose();
					_currentParcelCancelSource = null;
				}
				_cancelSource.dispose();
				_hasParcel.dispose();
			}
		}

		public ActionTwo<Object,DocumentParseCompleteEventArgs> getResultsReady() {
			return resultsReady;
		}

		public void setResultsReady(ActionTwo<Object,DocumentParseCompleteEventArgs> resultsReady) {
			this.resultsReady = resultsReady;
		}

	}

	private static class BackgroundThread extends ThreadStateBase {
		private MainThreadState _main;
		private Thread _backgroundThread;
		private CancellationToken _shutdownToken;
		private RazorEngineHost _host;
		private String _fileName;
		private Block _currentParseTree;
		private java.util.List<TextChange> _previouslyDiscarded = new java.util.ArrayList<TextChange>();

		public BackgroundThread(MainThreadState main, RazorEngineHost host, String fileName) {
			// Run on MAIN thread!
			_main = main;
			_backgroundThread = new Thread(()->workerLoop());
			_shutdownToken = _main.getCancelToken();
			_host = host;
			_fileName = fileName;

			setThreadId(_backgroundThread.getId());
		}

		// **** ANY THREAD ****
		public final void start() {
			_backgroundThread.start();
		}

		// **** BACKGROUND THREAD ****
		private void workerLoop() {
			// Long elapsedMs = null;
			String fileNameOnly = Path.GetFileName(_fileName);
			 
			// #if EDITOR_TRACING
			// Stopwatch sw = new Stopwatch();
			// #endif

			try {
				RazorEditorTrace.traceLine(RazorResources.getTrace_BackgroundThreadStart(), fileNameOnly);
				ensureOnThread();
				while (!_shutdownToken.isCancellationRequested()) {
					// Grab the parcel of work to do
					WorkParcel parcel = _main.getParcel();
					if (!parcel.getChanges().isEmpty()) {
						RazorEditorTrace.traceLine(RazorResources.getTrace_ChangesArrived(), fileNameOnly,
								parcel.getChanges().size());
						DocumentParseCompleteEventArgs args = null;
						 
						 
						 
							// using (var linkedCancel =
						// CancellationTokenSource.CreateLinkedTokenSource(_shutdownToken,
						// parcel.CancelToken))
						CancellationTokenSource linkedCancel = CancellationTokenSource.CreateLinkedTokenSource(_shutdownToken,
								parcel.getCancelToken());
						try {
							if (!linkedCancel.IsCancellationRequested()) {
								// Collect ALL changes
								// C# TO JAVA CONVERTER TODO TASK: There is
								// no preprocessor in Java:
								// #if EDITOR_TRACING
								if (_previouslyDiscarded != null && _previouslyDiscarded.size()>0) {
									RazorEditorTrace.traceLine(RazorResources.getTrace_CollectedDiscardedChanges(),
											fileNameOnly, _previouslyDiscarded.size());
								}
								// #endif
								List<TextChange> allChanges=new ArrayList<TextChange>();

								if (_previouslyDiscarded != null) {
									//allChanges = _previouslyDiscarded.addAll(parcel.getChanges());
									allChanges.addAll(_previouslyDiscarded);
									allChanges.addAll(parcel.getChanges());
											
								} else {
									allChanges = parcel.getChanges();
								}

								TextChange finalChange = allChanges.get(allChanges.size()-1);
								// C# TO JAVA CONVERTER TODO TASK: There is
								// no preprocessor in Java:
								// #if EDITOR_TRACING
								// sw.Start();
								// #endif
								GeneratorResults results = parseChange(finalChange.getNewBuffer(),
										linkedCancel.getToken());
								// C# TO JAVA CONVERTER TODO TASK: There is
								// no preprocessor in Java:
								// #if EDITOR_TRACING
								//sw.Stop();
								//elapsedMs = sw.ElapsedMilliseconds;
								//sw.Reset();
								// #endif
								//RazorEditorTrace.TraceLine(RazorResources.getTrace_ParseComplete(), fileNameOnly,
								//		elapsedMs != null ? elapsedMs.toString(CultureInfo.InvariantCulture) : "?");

								if (results != null && !linkedCancel.IsCancellationRequested()) {
									// Clear discarded changes list
									_previouslyDiscarded = null;

									// Take the current tree and check for
									// differences
									// C# TO JAVA CONVERTER TODO TASK: There
									// is no preprocessor in Java:
									// #if EDITOR_TRACING
									// sw.Start();
									// #endif
									boolean treeStructureChanged = _currentParseTree == null
											|| treesAreDifferent(_currentParseTree, results.getDocument(),
													allChanges, parcel.getCancelToken());
									// C# TO JAVA CONVERTER TODO TASK: There
									// is no preprocessor in Java:
									// #if EDITOR_TRACING
									// sw.Stop();
									// elapsedMs = sw.ElapsedMilliseconds;
									// sw.Reset();
									// #endif
									_currentParseTree = results.getDocument();
									//RazorEditorTrace.TraceLine(RazorResources.getTrace_TreesCompared(),
										//	fileNameOnly, elapsedMs != null
										//			? elapsedMs.toString(CultureInfo.InvariantCulture) : "?",
										//	treeStructureChanged);

									// Build Arguments
									DocumentParseCompleteEventArgs tempVar = new DocumentParseCompleteEventArgs();
									tempVar.setGeneratorResults(results);
									tempVar.setSourceChange(finalChange);
									tempVar.setTreeStructureChanged(treeStructureChanged);
									args = tempVar;
								} else {
									// Parse completed but we were cancelled
									// in the mean time. Add these to the
									// discarded changes set
									RazorEditorTrace.traceLine(RazorResources.getTrace_ChangesDiscarded(),
											fileNameOnly, allChanges.size());
									_previouslyDiscarded = allChanges;
								}

								// C# TO JAVA CONVERTER TODO TASK: There is
								// no preprocessor in Java:
								// #if CHECK_TREE
								if (args != null) {
									// Rewind the buffer and sanity check
									// the line mappings
									finalChange.getNewBuffer().setPosition(0);
									finalChange.getNewBuffer().ReadToEnd()
											.split("\r\n" );
								}
								// #endif
							}
						} finally {
							linkedCancel.dispose();
						}
						if (args != null) {
							_main.returnParcel(args);
						}
					} else {
						RazorEditorTrace.traceLine(RazorResources.getTrace_NoChangesArrived(), fileNameOnly,
								parcel.getChanges().size());
						Thread.yield();
					}
				}
			} finally {
				RazorEditorTrace.traceLine(RazorResources.getTrace_BackgroundThreadShutdown(), fileNameOnly);

				// Clean up main thread resources
				_main.dispose();
			}
		}

		private GeneratorResults parseChange(ITextBuffer buffer, CancellationToken token) {
			ensureOnThread();

			// Create a template engine
			RazorTemplateEngine engine;
			try {
				engine = new RazorTemplateEngine(_host);
				// Seek the buffer to the beginning
				buffer.setPosition(0);

				return engine.generateCode(buffer, null, null, _fileName, token);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}

			
		}
	}

	private static class WorkParcel {
		public WorkParcel(java.util.List<TextChange> changes, CancellationToken cancelToken) {
			setChanges(changes);
			setCancelToken(cancelToken);
		}

		private CancellationToken privateCancelToken;

		public final CancellationToken getCancelToken() {
			return privateCancelToken;
		}

		private void setCancelToken(CancellationToken value) {
			privateCancelToken = value;
		}

		private java.util.List<TextChange> privateChanges;

		public final java.util.List<TextChange> getChanges() {
			return privateChanges;
		}

		private void setChanges(java.util.List<TextChange> value) {
			privateChanges = value;
		}
	}


}