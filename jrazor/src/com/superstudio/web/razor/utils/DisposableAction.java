package com.superstudio.web.razor.utils;

import com.superstudio.commons.IDisposable;
import com.superstudio.commons.csharpbridge.action.Action;
import com.superstudio.commons.exception.ArgumentNullException;


public class DisposableAction implements IDisposable {
	private Action _action;

	public DisposableAction(Action action)  {
		if (action == null) {
			////throw new ArgumentNullException("action");
		}
		_action = action;
	}

	@Override
	public  void dispose() {
		dispose(true);
		System.gc();
		// GC.SuppressFinalize(this);
	}

	protected void dispose(boolean disposing) {
		// If we were disposed by the finalizer it's because the user didn't use
		// a "using" block, so don't do anything!
		if (disposing) {
			_action.execute();
		}
	}

	
}