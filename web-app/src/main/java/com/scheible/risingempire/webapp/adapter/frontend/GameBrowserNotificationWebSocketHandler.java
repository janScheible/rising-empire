package com.scheible.risingempire.webapp.adapter.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheible.risingempire.webapp.notification.NotificationChannel;
import com.scheible.risingempire.webapp.notification.NotificationService;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class GameBrowserNotificationWebSocketHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(GameBrowserNotificationWebSocketHandler.class);

	private final ObjectMapper objectMapper;
	private final NotificationService notificationService;

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The objects are not mutated.")
	public GameBrowserNotificationWebSocketHandler(final ObjectMapper objectMapper,
			final NotificationService notificationService) {
		this.objectMapper = objectMapper;
		this.notificationService = notificationService;
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
		final WebSocketSession concurrentSession = new ConcurrentWebSocketSessionDecorator(session, 1000, 1024);
		final NotificationChannel notificationChannel = new WebSocketNotification(concurrentSession, objectMapper);
		notificationService.registerBroadcastChannel(session.getId(), notificationChannel);
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
		notificationService.unregisterBroadcastChannel(session.getId());
	}

	@Override
	public void handleTransportError(final WebSocketSession session, final Throwable exception) throws Exception {
		logger.warn("WebSocket transport error.", exception);
	}
}
