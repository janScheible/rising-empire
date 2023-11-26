package com.scheible.risingempire.webapp.adapter.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheible.risingempire.webapp.notification.NotificationChannel;
import com.scheible.risingempire.webapp.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author sj
 */
public class GameBrowserNotificationWebSocketHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(GameBrowserNotificationWebSocketHandler.class);

	private final ObjectMapper objectMapper;

	private final NotificationService notificationService;

	public GameBrowserNotificationWebSocketHandler(ObjectMapper objectMapper, NotificationService notificationService) {
		this.objectMapper = objectMapper;
		this.notificationService = notificationService;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		WebSocketSession concurrentSession = new ConcurrentWebSocketSessionDecorator(session, 1000, 1024);
		NotificationChannel notificationChannel = new WebSocketNotification(concurrentSession, this.objectMapper);
		this.notificationService.registerBroadcastChannel(session.getId(), notificationChannel);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		this.notificationService.unregisterBroadcastChannel(session.getId());
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		logger.warn("WebSocket transport error.", exception);
	}

}
