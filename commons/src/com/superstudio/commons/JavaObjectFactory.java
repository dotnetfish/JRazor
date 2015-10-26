package com.superstudio.commons;

public class JavaObjectFactory implements IWebObjectFactory {

	private String className;
	public JavaObjectFactory(String className) {
		// TODO Auto-generated constructor stub
		this.className=className;
	}

	@Override
	public Object CreateInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return Class.forName(className).newInstance();
	}

}
