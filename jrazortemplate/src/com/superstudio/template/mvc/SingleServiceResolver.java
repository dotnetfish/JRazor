package com.superstudio.template.mvc;

import java.util.function.Supplier;

import com.superstudio.template.mvc.BuildManagerTemplateEngine.DefaultTemplatePageActivator;

public class SingleServiceResolver<T> implements IResolver<T> {

	private Supplier<T> resolver;
	public SingleServiceResolver(Supplier<T> supplier, DefaultTemplatePageActivator defaultTemplatePageActivator, String string) {
		resolver=supplier;
	}

	@Override
	public T getCurrent() {
		// TODO Auto-generated method stub
		return resolver.get();
	}

}
