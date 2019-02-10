package com.superstudio.codedom;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CollectionBase<T> extends ArrayList<T> {
	public void copyTo(T[] array,int index){
		subList(index,size()-index-1).toArray(array);
	}
	


}
