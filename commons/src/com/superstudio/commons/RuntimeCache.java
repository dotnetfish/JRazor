package com.superstudio.commons;

import java.util.concurrent.ConcurrentHashMap;

public class RuntimeCache {

	//private static RuntimeCache instance=null;
	private static RuntimeCache instance=null;
	public static synchronized RuntimeCache getInstance(){
		if(instance==null){
			createInstance();
		}
		return instance;
	}
	
	private static synchronized void createInstance(){
		if(instance==null){
			instance=new RuntimeCache();
		}
		
	}
	
	private ConcurrentHashMap<String,Object> caches;
	
	private RuntimeCache(){
		caches=new ConcurrentHashMap<String,Object>();
	}
	
	public  Object get(String key){
		return caches.get(key);
	}
	
	public  void set(String key,Object object){
		caches.put(key, object);
	}
}
