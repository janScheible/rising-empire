package com.scheible.risingempire.webapp.adapter.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheible.risingempire.webapp.notification.NotificationChannel;

/**
 *
 * @author sj
 */
class WebSocketNotification implements NotificationChannel {

	private final WebSocketSession webSocketSession;
	private final ObjectMapper objectMapper;

	WebSocketNotification(final WebSocketSession webSocketSession, final ObjectMapper objectMapper) {
		this.webSocketSession = webSocketSession;
		this.objectMapper = objectMapper;
	}

	@Override
	public void sendMessage(final String type, final Map<String, Object> payload) throws IOException {
		final Map<String, Object> message = new HashMap<>(payload);
		message.put("type", type);
		final String json = objectMapper.writeValueAsString(message);
		webSocketSession.sendMessage(new TextMessage(json));
	}
}
