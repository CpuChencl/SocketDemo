package com.ccl.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements Runnable{
	
	private int port;

	CountDownLatch latch;
	
	AsynchronousServerSocketChannel asynchronousServerSocketChannel;
	
	public AsyncTimeServerHandler(int port){
		this.port = port;
		try {
			//创建异步服务端通道
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			//绑定端口号
			asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
			System.out.println("time server start on port: "+port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		//latch:在完成一组正在执行的操作之前，允许当前的线程一直阻塞(参数1表示一组操作)
		latch = new CountDownLatch(1);
		doAccept();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//接收消息
	public void doAccept(){
		asynchronousServerSocketChannel.accept(this,new AcceptCompletionHandler());
	}

}
