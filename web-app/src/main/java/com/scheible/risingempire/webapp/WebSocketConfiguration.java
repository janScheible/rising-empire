package com.scheible.risingempire.webapp;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheible.risingempire.webapp.adapter.frontend.NotificationWebSocketHandler;
import com.scheible.risingempire.webapp.notification.NotificationService;

/**
 *
 * @author sj
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer, ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
		final NotificationWebSocketHandler handler = new NotificationWebSocketHandler(
				applicationContext.getBean(ObjectMapper.class), applicationContext.getBean(NotificationService.class));
		registry.addHandler(handler, "/frontend/notifications").setAllowedOrigins("http://localhost",
				"https://localhost", "http://localhost:8080", "https://risingempire.de");
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
