package com.ccl.aio.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ReadCompletionHandler implements CompletionHandler<Integer,ByteBuffer>{

	//主要用于读取半包消息和发送应答
	private AsynchronousSocketChannel channel;
	
	public ReadCompletionHandler(AsynchronousSocketChannel channel){
		this.channel = channel;
	}
	
	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		attachment.flip();
		byte[] body = new byte[attachment.remaining()];
		attachment.get(body);
		try {
			String req = new String(body,"UTF-8");
			System.out.println("the time server recived: "+req);
			String current = "QUERY TIME ORDER".equalsIgnoreCase(req) ? System.currentTimeMillis() + "" : "BAD ORDER";
			doWrite(current);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void doWrite(String resp){
		if(resp != null && resp.trim().length() > 0){
			 byte [] bytes = resp.getBytes();
			 ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
			 buffer.put(bytes);
			 buffer.flip();
			 channel.write(buffer,buffer,new CompletionHandler<Integer, ByteBuffer>() {

				@Override
				public void completed(Integer result, ByteBuffer attachment) {
					//如果没有发完，继续发送
					if(attachment.hasRemaining()){
						channel.write(attachment,attachment,this);
					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					try {
						channel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

}
