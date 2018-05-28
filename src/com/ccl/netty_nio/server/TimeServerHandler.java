package com.ccl.netty_nio.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req,"UTF-8");
		System.out.println("revice client order: "+body);
		String current = "QUERY TIME ORDER".equalsIgnoreCase(body) ? System.currentTimeMillis() + "" : "BAD ORDER";
		ByteBuf resp = Unpooled.copiedBuffer(current.getBytes());
		ctx.write(resp);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		ctx.close();
	}
	
}