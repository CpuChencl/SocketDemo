package com.ccl.netty_nio.marshalling;

import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

public class MarshallingCodeCFactory {

	public static MarshallingDecoder buildMarshallingDecoder(){
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration marshallingConfig = new MarshallingConfiguration();
		marshallingConfig.setVersion(5);
		UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory,marshallingConfig);
		MarshallingDecoder decoder = new MarshallingDecoder(provider,1024);
		return decoder;
	}
	
	public static MarshallingEncoder buildMarshallingEncoder(){
		final MarshallerFactory marshallFactory = Marshalling.getMarshallerFactory("serial");
		final MarshallingConfiguration config = new MarshallingConfiguration();
		config.setVersion(5);
		MarshallerProvider provider = new DefaultMarshallerProvider(marshallFactory, config);
		MarshallingEncoder encoder = new MarshallingEncoder(provider);
		return encoder;
	}
}
