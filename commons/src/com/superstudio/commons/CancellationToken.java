package com.superstudio.commons;

public class CancellationToken {
	public static final CancellationToken None = null;


	public CancellationToken(boolean canceled) {

	}

	public static CancellationToken None(){
		return null;
	}

	public boolean CanBeCanceled(){
		return false;
	}
	public boolean isCancellationRequested()
	{
		return false;
	}

	

	// public WaitHandle WaitHandle { get; }
	@Override
	public boolean equals(Object other) {
		return false;

	}

	public boolean equals(CancellationToken other) {
		return false;
	}


    public  int getHashCode() {
	return 0;
}

	public void ThrowIfCancellationRequested() {
		// TODO Auto-generated method stub
		
	}

	/*public CancellationTokenRegistration Register(Action callback);

	public CancellationTokenRegistration Register(Action<object> callback, object state);

	public CancellationTokenRegistration Register(Action callback, bool useSynchronizationContext);

	public CancellationTokenRegistration Register(Action<object> callback, object state,
			bool useSynchronizationContext);[

	TargetedPatchingOptOut("Performance critical to inline across NGen image boundaries")]

	public void ThrowIfCancellationRequested();

	public static bool operator==(
	CancellationToken left, CancellationToken right);
	public static bool operator!=(
	CancellationToken left, CancellationToken right);*/
}