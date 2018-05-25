package com.ccl.bio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.ccl.bio.handler.TimeServerHandlerBIO;

/**
 * 同步阻塞模式
 * @author admin
 *
 */
public class TimeServerBIO {

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
			while(true){
				socket = server.accept();
				new Thread(new TimeServerHandlerBIO(socket)).start();
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
