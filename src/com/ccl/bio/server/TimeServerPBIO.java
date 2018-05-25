package com.ccl.bio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.ccl.bio.handler.TimeServerHandlerBIO;
import com.ccl.bio.handler.TimeServerHandlerExcutePool;

/**
 * 同步非阻塞
 * 伪异步模式
 * @author admin
 *
 */
public class TimeServerPBIO {

	public static void main(String[] args) throws IOException {
		int port = 8080;
		if(args != null && args.length > 0){
			port = Integer.valueOf(args[0]);
		}
		
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("the time server is start on port "+port);
			Socket socket = null;
			TimeServerHandlerExcutePool excutePool = new TimeServerHandlerExcutePool(10, 10000);
			while(true){
				socket = server.accept();
				excutePool.execute(new TimeServerHandlerBIO(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(server != null){
				System.out.println("the time server close");
				server.close();
				server = null;
			}
		}
	}
}
