package com.superstudio.commons;



public class CancellationTokenSource implements AutoCloseable{

	public CancellationToken getToken() {
		// TODO Auto-generated method stub
		return null;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void close(){
		dispose();
	}

	public void Cancel() {
		// TODO Auto-generated method stub
		
	}

	public static CancellationTokenSource CreateLinkedTokenSource(CancellationToken _shutdownToken, CancellationToken cancelToken) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean IsCancellationRequested() {
		// TODO Auto-generated method stub
		return false;
	}

}
