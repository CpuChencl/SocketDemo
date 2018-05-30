package com.ccl.netty_nio.echo;

import com.ccl.netty_nio.messagepack.MsgpackDecoder;
import com.ccl.netty_nio.messagepack.MsgpackEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;

public class EchoClient {

	public void connect(int port,String host) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
					
					//使用分隔符解码器
					//ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
					
					//使用固定长度解码器(按照20个字节长度进行截取，不足20的等待)
					//ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
					//ch.pipeline().addLast(new StringDecoder());
					//使用messagepack进行编解码
					ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65350, 0, 2,0,2));
					ch.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
					ch.pipeline().addLast("framerEncoder",new LengthFieldPrepender(3));
					ch.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
					
					ch.pipeline().addLast(new EchoClientHandler(3));
				}
			});
			
			ChannelFuture f = b.connect(host,port).sync();
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		try {
			new EchoClient().connect(8080, "127.0.0.1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
