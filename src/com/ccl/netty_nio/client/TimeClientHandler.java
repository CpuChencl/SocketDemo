package com.ccl.netty_nio.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TimeClientHandler extends SimpleChannelInboundHandler<Object> {
	
	private final ByteBuf firstMsg;
	
	public TimeClientHandler(){
		String order ="trade";
		byte[] req = order.getBytes();
		firstMsg = Unpooled.buffer(req.length);
		firstMsg.writeBytes(req);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx){
		ctx.writeAndFlush(firstMsg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		System.out.println("unexpected exception from downstream : "+cause.getMessage());
		ctx.close();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String body = (String)msg;
		System.out.println("now is: "+ body);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		
	}
}
