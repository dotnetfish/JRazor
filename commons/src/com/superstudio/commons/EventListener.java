package com.superstudio.commons;

public interface EventListener<TArgs extends EventArgs> {
	void execute(Object sender,TArgs args);
}
