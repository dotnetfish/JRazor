package com.superstudio.commons;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskFactory {
	private ScheduledThreadPoolExecutor exec = null;
	
	private static Object syncLock=new Object();
	private static TaskFactory instance=null;
	public static TaskFactory getInstance(){
		if(instance==null){
			synchronized(syncLock){
				if(instance==null){
					instance=new TaskFactory();
				}
			}
		}
		return instance;
	}
	static {
		
	}
	private  TaskFactory(){
		exec=new ScheduledThreadPoolExecutor(20);
	}
	
	public void StartNew(Runnable task){
		exec.schedule(task, 0, TimeUnit.MILLISECONDS);
	}
	
	
}
