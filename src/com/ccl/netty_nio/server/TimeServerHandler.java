package com.ccl.netty_nio.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
		String body = (String) msg;
		System.out.println("revice client order: "+body);
		String current = "QUERY TIME ORDER".equalsIgnoreCase(body) ? System.currentTimeMillis() + "" : "BAD ORDER";
		current = current + System.getProperty("line.separator");
		ByteBuf resp = Unpooled.copiedBuffer(current.getBytes());
		//write方法只是将消息写入发送缓冲数组中
		ctx.write(resp);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		ctx.close();
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
		//将消息对中的消息写入SocketChannel中发送给对方
		ctx.flush();
	}
	
}
