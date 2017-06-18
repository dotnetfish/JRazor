package com.superstudio.commons;

public interface IWebObjectFactory {

	Object createInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException;

}
