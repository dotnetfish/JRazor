package com.superstudio.commons;

import java.util.function.Consumer;

public class SynchronizationContext {

	public void post(Consumer<Object> callBack, Object state) {

		 callBack.accept(state);
	}

}
