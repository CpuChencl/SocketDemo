package com.ccl.aio.server;

public class TimeServer {

	public static void main(String[] args) {
		int port = 8080;
		if(args != null && args.length > 0){
			port = Integer.valueOf(port);
		}
		AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
		new Thread(timeServer,"AIO-TimeServer-001").start();
	}
}
