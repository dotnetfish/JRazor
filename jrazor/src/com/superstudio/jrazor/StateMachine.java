package com.superstudio.jrazor;





public abstract class StateMachine<TReturn>
{
	
 
	public interface State {
		StateResult<?> execute();
	}


	protected abstract State getStartState();

	private State privateCurrentState;
	protected final State getCurrentState()
	{
		return privateCurrentState;
	}
	protected final void setCurrentState(State value)
	{
		privateCurrentState = value;
	}

	protected TReturn turn()
	{
		if (getCurrentState() != null)
		{
			StateResult<?> result;
			do
			{
				// Keep running until we get a null result or output
				result = getCurrentState().execute();
				setCurrentState(result.getNext());
			}
			while (result != null && !result.getHasOutput());

			return (TReturn) result.getOutput();
		}
		return null;
	}

	/** 
	 Returns a result indicating that the machine should stop executing and return null output.
	 
	*/
	protected final StateResult<TReturn> stop()
	{
		return null;
	}

	/** 
	 Returns a result indicating that this state has no output and the machine should immediately invoke the specified state
	 
	 
	 By returning no output, the state machine will invoke the next state immediately, before returning
	 controller to the caller of <see cref="Turn"/>
	 
	*/
	protected final StateResult<TReturn> transition(State newState)
	{
		return new StateResult<TReturn>(newState);
	}

	/** 
	 Returns a result containing the specified output and indicating that the next call to
	 <see cref="Turn"/> should invoke the provided state.
	 
	*/
	protected final StateResult<TReturn> transition(TReturn output, State newState)
	{
		return new StateResult<TReturn>(output, newState);
	}

	/** 
	 Returns a result indicating that this state has no output and the machine should remain in this state
	 
	 
	 By returning no output, the state machine will re-invoke the current state again before returning
	 controller to the caller of <see cref="Turn"/>
	 
	*/
	protected final StateResult<TReturn> stay()
	{
		return new StateResult<TReturn>(getCurrentState());
	}

	/** 
	 Returns a result containing the specified output and indicating that the next call to
	 <see cref="Turn"/> should re-invoke the current state.
	 
	*/
	protected final StateResult<TReturn> stay(TReturn output)
	{
		return new StateResult<TReturn>(output, getCurrentState());
	}

	protected static class StateResult<TReturn>
	{
		public StateResult(State next)
		{
			setHasOutput(false);
			setNext(next);
		}

		public StateResult(TReturn output, State next)
		{
			setHasOutput(true);
			setOutput(output);
			setNext(next);
		}

		private boolean privateHasOutput;
		public final boolean getHasOutput()
		{
			return privateHasOutput;
		}
		public final void setHasOutput(boolean value)
		{
			privateHasOutput = value;
		}
		private TReturn privateOutput;
		public final TReturn getOutput()
		{
			return privateOutput;
		}
		public final void setOutput(TReturn value)
		{
			privateOutput = value;
		}
		private State privateNext;
		public final State getNext()
		{
			return privateNext;
		}
		public final void setNext(State value)
		{
			privateNext = value;
		}
	}
}