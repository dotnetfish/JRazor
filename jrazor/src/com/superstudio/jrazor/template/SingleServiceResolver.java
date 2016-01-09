package com.superstudio.jrazor.template;

import java.util.function.Supplier;

import com.superstudio.jrazorengine.IResolver;
import com.superstudio.jrazorengine.BuildManagerViewEngine.DefaultViewPageActivator;

public class SingleServiceResolver<T> implements IResolver<T> {

	private Supplier<T> resolver;
	public SingleServiceResolver(Supplier<T> supplier, DefaultViewPageActivator defaultViewPageActivator, String string) {
		resolver=supplier;
	}

	@Override
	public T getCurrent() {
		// TODO Auto-generated method stub
		return resolver.get();
	}

}
