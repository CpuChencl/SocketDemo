package com.ccl.netty_nio.messagepack;

import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * 解码器
 * @author admin
 *
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf>{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		final byte[] array;
		final int length = msg.readableBytes();
		array = new byte[length];
		//从msg中获取需要解码的byte数组
		msg.getBytes(msg.readerIndex(), array, 0, length);
		MessagePack msgpack = new MessagePack();
		//调用read方法将byte数组解码为object,将解码后的对象加到返回list中
		out.add(msgpack.read(array));
	}

}
