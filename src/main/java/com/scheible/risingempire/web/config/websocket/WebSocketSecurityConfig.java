package com.scheible.risingempire.web.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * @author sj
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages
			.simpMessageDestMatchers("/queue/**","/topic/**").denyAll()
			.simpSubscribeDestMatchers("/queue/**/*-user*","/topic/**/*-user*").denyAll()
			.anyMessage().authenticated();
	}
}