package com.ccl.netty_nio.echo;

import com.ccl.bean.UserInfo;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class EchoClientHandler extends SimpleChannelInboundHandler<Object>{

	private static final String msg = "hi it's client";
	
	private final int number;
	
	public EchoClientHandler(int number){
		this.number = number;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//		System.out.println((String)msg);
		System.out.println("from server msg:"+msg);
		ctx.writeAndFlush(msg);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx){
//		ctx.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));
		UserInfo[] userInfo = userArray();
		for(UserInfo u : userInfo){
			ctx.writeAndFlush(u);
		}
		ctx.flush();
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
	
	private UserInfo [] userArray(){
		UserInfo[] uArray = new UserInfo[number];
		UserInfo userInfo = null;
		for(int i=0; i<number; i++){
			userInfo = new UserInfo();
			userInfo.setAge(18);
			userInfo.setUserName("学生妹"+i);
			userInfo.setMobile("182123456"+i);
			uArray[i] = userInfo;
		}
		return uArray;
	}

}
