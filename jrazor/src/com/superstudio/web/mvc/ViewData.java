package com.superstudio.web.mvc;

import java.util.HashMap;
import java.util.Map;

public class ViewData  {
	private Map<String,Object> map;
	
	private Object model;
	
	public ViewData(Object model){
		this.model=model;
		this.map=new HashMap<String,Object>();
	}
	
	public ViewData(Object model,Map<String,Object> viewData){
		this.model=model;
		this.map=viewData;
	}
	public <T> T get(String key){
		return (T)(map.get(key));
	}
	
	public void set(String key,Object obj){
		map.put(key, obj);
	}
	
	public Object getModel(){
		return model;
		
	}
	
	
}
