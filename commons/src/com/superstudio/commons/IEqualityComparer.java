package com.superstudio.commons;

public interface IEqualityComparer<T> {
	default boolean equals(T x, T y){
		return x.equals(y);
	}

	default int hashCode(T obj){
		return obj.hashCode();
	}
}
