package com.superstudio.web.razor.text;


public class LookaheadToken implements AutoCloseable
{
	private Runnable _cancelAction;
	private boolean _accepted;

	public LookaheadToken(Runnable cancelAction)
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
			_cancelAction.run();
		}
	}

	


	public void dispose() {
		// TODO Auto-generated method stub
		dispose(true);
		//GC.SuppressFinalize(this);
		System.gc();
	}
}