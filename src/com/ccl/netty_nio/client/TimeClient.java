package com.ccl.netty_nio.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeClient {

	public void connect(int port,String host) {
		//配置客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class)
		.option(ChannelOption.TCP_NODELAY, true)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				//LineBasedFrameDecoder + StringDecoder 解码器
				ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
				ch.pipeline().addLast(new StringDecoder());
				ch.pipeline().addLast(new TimeClientHandler());
			}
		});
		ChannelFuture f;
		try {
			//发起异步连接操作
			f = b.connect(host,port).sync();
			//等待客户端链接关闭
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		int port = 8080;
		new TimeClient().connect(port, "127.0.0.1");
	}
}
