package com.scheible.risingempire.webapp.adapter.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheible.risingempire.webapp.notification.NotificationChannel;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author sj
 */
class WebSocketNotification implements NotificationChannel {

	private final WebSocketSession webSocketSession;

	private final ObjectMapper objectMapper;

	WebSocketNotification(WebSocketSession webSocketSession, ObjectMapper objectMapper) {
		this.webSocketSession = webSocketSession;
		this.objectMapper = objectMapper;
	}

	@Override
	public void sendMessage(String type, Map<String, Object> payload) throws IOException {
		Map<String, Object> message = new HashMap<>(payload);
		message.put("type", type);
		String json = this.objectMapper.writeValueAsString(message);
		this.webSocketSession.sendMessage(new TextMessage(json));
	}

}
