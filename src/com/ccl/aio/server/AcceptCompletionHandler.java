package com.ccl.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,AsyncTimeServerHandler>{

	/**
	 * 获取AsyncTimeServerHandler的实例并再次调用accept方法.
	 * 原因是调用AsynchronousSocketChannel的accept方法后，如果有新的客户端连接接入,
	 * 系统将会回调我们传入的CompletionHandler实例的completed方法，表示新的客户端已经接入成功。
	 * 因为一个AsynchronousSocketChannel可以接收成千上万个客户端,所以需要继续调用它的accept方法,
	 * 接收其他的客户端连接，最终形成一个循环。
	 * 每当接收一个客户读连接成功后，在异步接收新的客户端连接。
	 */
	@Override
	public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
		attachment.asynchronousServerSocketChannel.accept(attachment,this);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		/**
		 * buffer:接收缓冲区
		 * ReadCompletionHandler作为接收通知回调的业务handler
		 */
		result.read(buffer, buffer, new ReadCompletionHandler(result));
	}

	@Override
	public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
		exc.printStackTrace();
		attachment.latch.countDown();
	}

}
