package com.ccl.bio.handler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeServerHandlerExcutePool{

	private ExecutorService executor;
	
	public TimeServerHandlerExcutePool(int maxPoolSize,int queueSize){
		executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 
				maxPoolSize, 120L, TimeUnit.SECONDS, new ArrayBlockingQueue<java.lang.Runnable>(queueSize));
	}
	
	public void execute(java.lang.Runnable task){
		executor.execute(task);
	}

}
