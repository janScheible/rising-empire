package com.scheible.risingempire.webapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheible.risingempire.webapp.adapter.frontend.GameBrowserNotificationWebSocketHandler;
import com.scheible.risingempire.webapp.adapter.frontend.GameNotificationWebSocketHandler;
import com.scheible.risingempire.webapp.notification.NotificationService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author sj
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer, ApplicationContextAware {

	private static final String[] ALLOWED_ORIGINS = new String[] { "http://localhost", "https://localhost",
			"http://localhost:8080", "https://risingempire.de" };

	private ApplicationContext applicationContext;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		GameNotificationWebSocketHandler gameHandler = new GameNotificationWebSocketHandler(
				this.applicationContext.getBean(ObjectMapper.class),
				this.applicationContext.getBean(NotificationService.class));
		registry.addHandler(gameHandler, "/game/notifications").setAllowedOrigins(ALLOWED_ORIGINS);

		GameBrowserNotificationWebSocketHandler gameBrowserHandler = new GameBrowserNotificationWebSocketHandler(
				this.applicationContext.getBean(ObjectMapper.class),
				this.applicationContext.getBean(NotificationService.class));
		registry.addHandler(gameBrowserHandler, "/game-browser/notifications").setAllowedOrigins(ALLOWED_ORIGINS);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
