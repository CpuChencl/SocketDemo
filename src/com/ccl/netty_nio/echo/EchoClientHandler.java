package com.ccl.netty_nio.echo;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class EchoClientHandler extends SimpleChannelInboundHandler<Object>{

	private static final String msg = "hi it's client";
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println((String)msg);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx){
		ctx.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}

}
