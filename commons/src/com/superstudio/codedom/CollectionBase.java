package com.superstudio.codedom;

import java.util.ArrayList;

public abstract class CollectionBase<T> extends ArrayList<T>{
	public void copyTo(T[] array,int index){
		subList(index,size()-index-1).toArray(array);
	}
	
	public void Clear(){
		//delete(0,size());
		clear();
	}
	
}
