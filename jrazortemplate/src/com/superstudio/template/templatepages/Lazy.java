package com.superstudio.template.templatepages;

import java.util.function.Supplier;

public final class Lazy<T> {

	private Supplier<T> creator;
	private T value;
	
	public Lazy(Supplier<T> supplier) {
		setCreator(supplier);
	}
	/*public Supplier<T> getCreator() {
		return creator;
	}
	*/
	protected void setCreator(Supplier<T> creator) {
		this.creator = creator;
	}

	public T getValue() {
		if(value==null){
			value=creator.get();
		}
		return value;
	}

	

}
