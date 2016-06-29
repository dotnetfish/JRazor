package com.superstudio.template.templatepages;

public class PositionTagged<T> {

	private  T obj;
	private  int position;
	public  PositionTagged(T obj,int position){
		this.obj=obj;
		this.position=position;
	}
	public T getValue() {
		// TODO Auto-generated method stub
		return obj;
	}

	public int getPosition() {
		// TODO Auto-generated method stub
		return position;
	}

}
