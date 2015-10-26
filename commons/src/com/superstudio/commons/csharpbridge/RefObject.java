package com.superstudio.commons.csharpbridge;

public class RefObject<T> {
	private T refObj;

	public RefObject(T obj) {
		this.setRefObj(obj);
	}

	public T getRefObj() {
		return refObj;
	}

	public void setRefObj(T refObj) {
		this.refObj = refObj;
	}
}
