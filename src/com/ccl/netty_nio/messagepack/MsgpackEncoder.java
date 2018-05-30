package com.ccl.netty_nio.messagepack;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 * @author admin
 *
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		MessagePack msgpack = new MessagePack();
		//序列化
		byte[] raw = msgpack.write(msg);
		out.writeBytes(raw);
	}

}
