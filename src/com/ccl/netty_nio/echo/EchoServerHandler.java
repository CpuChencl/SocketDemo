package com.ccl.netty_nio.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
//		String body = (String)msg;
		System.out.println("recive client order: "+ msg);
//		body += "$_";
//		ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
		ctx.writeAndFlush(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
