package com.ccl.netty_nio.echo;

import com.ccl.netty_nio.messagepack.MsgpackDecoder;
import com.ccl.netty_nio.messagepack.MsgpackEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {

	public void bind(int port) throws Exception{
		EventLoopGroup boosGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(boosGroup,workGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 100)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
	
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
					
					//使用分隔符解码器
					//ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
					
					//使用固定长度解码器
					//ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
					//ch.pipeline().addLast(new StringDecoder());
					
					//使用messagepack进行编解码
					ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65350, 0, 2,0,2));
					ch.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
					ch.pipeline().addLast("framerEncoder",new LengthFieldPrepender(3));
					ch.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
					ch.pipeline().addLast(new EchoServerHandler());
				}
			});
			
			//绑定端口，等待同步成功
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally {
			boosGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		try {
			new EchoServer().bind(8080);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
