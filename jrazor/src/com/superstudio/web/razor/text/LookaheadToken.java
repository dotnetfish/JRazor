package com.superstudio.web.razor.text;


import com.superstudio.commons.csharpbridge.action.Action;


public class LookaheadToken implements AutoCloseable
{
	private Action _cancelAction;
	private boolean _accepted;

	public LookaheadToken(Action cancelAction)
	{
		_cancelAction = cancelAction;
	}

	public final void accept()
	{
		_accepted = true;
	}

	@Override
public  void  close(){
	dispose();
}
	protected void dispose(boolean disposing)
	{
		if (!_accepted)
		{
			_cancelAction.execute();
		}
	}

	


	public void dispose() {
		// TODO Auto-generated method stub
		dispose(true);
		//GC.SuppressFinalize(this);
		System.gc();
	}
}