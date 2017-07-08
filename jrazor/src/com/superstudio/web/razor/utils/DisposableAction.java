package com.superstudio.web.razor.utils;


public class DisposableAction implements AutoCloseable {
	private Runnable _action;

	public DisposableAction(Runnable action)  {
		if (action == null) {
			////throw new ArgumentNullException("action");
		}
		_action = action;
	}

	@Override
	public  void close() {
		close(true);
		//System.gc();
		// GC.SuppressFinalize(this);
	}

	protected void close(boolean disposing) {
		// If we were disposed by the finalizer it's because the user didn't use
		// a "using" block, so don't do anything!
		if (disposing) {
			_action.run();
		}
	}

	
}