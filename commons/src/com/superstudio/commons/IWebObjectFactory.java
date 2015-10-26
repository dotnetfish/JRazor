package com.superstudio.commons;

public interface IWebObjectFactory {

	Object CreateInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException;

}
