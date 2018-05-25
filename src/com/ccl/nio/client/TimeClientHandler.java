package com.ccl.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 异步非阻塞
 * NIO模式
 * @author admin
 *
 */
public class TimeClientHandler implements Runnable{
	
	private String host;
	private int port;
	private Selector selector;
	private SocketChannel channel;
	private volatile boolean stop;
	
	public TimeClientHandler(String host, int port){
		this.host = host == null ? "127.0.0.1" : host;
		this.port = port;
		try {
			selector = Selector.open();
			channel = SocketChannel.open();
			channel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	@Override
	public void run() {
		try {
			doConnect();
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
		while(!stop){
			try {
				selector.select(1000);
				Set<SelectionKey> selectionKey = selector.selectedKeys();
				Iterator<SelectionKey> it = selectionKey.iterator();
				SelectionKey key = null;
				while(it.hasNext()){
					key = it.next();
					it.remove();
					try {
						handleInput(key);
					} catch (Throwable e) {
						if(key != null){
							key.cancel();
							if(key.channel() != null){
								key.channel().close();
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		//多路复用关闭后，所有注册在上面的Channel和pipe都会被自动去注册并关闭，所以不需要重复释放资源
		if(selector != null){
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleInput(SelectionKey key) throws Throwable{
		if(key.isValid()){
			SocketChannel sc = (SocketChannel) key.channel();
			if(key.isConnectable()){
				if(sc.finishConnect()){
					sc.register(selector,SelectionKey.OP_READ);
					doWrite(sc);
				} else {
					System.exit(1);
				}
			}
			if(key.isReadable()){
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readByte = sc.read(readBuffer);
				if(readByte > 0){
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes,"UTF-8");
					System.out.println("recive server msg " + body);
					this.stop = true;
				} else if(readByte < 0){
					key.cancel();
					sc.close();
				}
			}
		}
	}
	
	private void doConnect() throws Throwable{
		if(channel.connect(new InetSocketAddress(host, port))){
			channel.register(selector, SelectionKey.OP_READ);
			doWrite(channel);
		} else {
			channel.register(selector, SelectionKey.OP_CONNECT);
		}
	}

	private void doWrite(SocketChannel sc) throws Throwable{
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		sc.write(writeBuffer);
		//判断缓冲区中的消息是否全部发送完成
		if(!writeBuffer.hasRemaining()){
			System.out.println("send req to server success");
		}
	}
}
