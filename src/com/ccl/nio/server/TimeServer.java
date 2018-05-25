package com.ccl.nio.server;

import java.io.IOException;

/**
 * 异步非阻塞
 * NIO模式
 * @author admin
 *
 */
public class TimeServer {

	public static void main(String[] args) throws IOException {
		int port = 8080;
		if(args != null && args.length > 0){
			port = Integer.valueOf(args[0]);
		}
		
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		new Thread(timeServer,"NIO-TimeServer-01").start();
	}
}
