package com.ccl.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 异步非阻塞
 * NIO模式
 * @author admin
 *
 */
public class MultiplexerTimeServer implements Runnable{

	private Selector selector;
	
	private ServerSocketChannel servChannel;
	
	private volatile boolean stop;
	
	public MultiplexerTimeServer(int port){
		try {
			//创建多路复用器
			selector = Selector.open();
			servChannel = ServerSocketChannel.open();
			//设置为非阻塞
			servChannel.configureBlocking(false);
			//绑定端口，backlog设置为1024
			servChannel.socket().bind(new InetSocketAddress(port),1024);
			//将servChannel注册到selector上监听accept事件
			servChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("time server start on port "+port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void stop(){
		this.stop = true;
	}
	
	@Override
	public void run() {
		while(!stop){
			try {
				//休眠时间为1秒
				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				SelectionKey key = null;
				//循环遍历selector
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
			}
		}
	}
	
	private void handleInput(SelectionKey key) throws Throwable{
		//通过selectkey操作位判断网络事件的类型
		if(key.isValid()){
			//处理新接入的消息
			if(key.isAcceptable()){
				ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
				//接收客户端的连接请求
				SocketChannel sc = ssc.accept();
				//设置为异步非阻塞
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}
			if(key.isReadable()){
				SocketChannel sc = (SocketChannel) key.channel();
				//创建一个1M的缓冲区
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				//读取请求码流
				int readByte = sc.read(readBuffer);
				if(readByte > 0){
					//读取客户端消息进行解码
					readBuffer.flip();
					//remaining判断是否还有数据
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("the time server recive " + body);
					String current = "QUERY TIME ORDER".equalsIgnoreCase(body) ? System.currentTimeMillis() + "" : "BAD ORDER";
					doWrite(sc,current);
				} else if(readByte < 0){
					key.cancel();
					sc.close();
				}
			}
		}
	}
	
	//将应答消息异步发送给客户端
	private void doWrite(SocketChannel sc, String resp) throws Throwable{
		if(resp != null && resp.trim().length() > 0){
			byte[] bytes = resp.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			sc.write(writeBuffer);
		}
	}

}
