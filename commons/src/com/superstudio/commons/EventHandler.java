package com.superstudio.commons;

public interface EventHandler<TArgs extends EventArgs> {
	void execute(Object sender,TArgs args);
}
