package com.superstudio.commons.csharpbridge.action;

@FunctionalInterface
public interface Func2<T1,T2,T3> {
	T3 execute(T1 t,T2 t2);

}
