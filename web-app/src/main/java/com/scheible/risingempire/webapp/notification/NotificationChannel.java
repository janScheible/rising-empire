package com.scheible.risingempire.webapp.notification;

import java.io.IOException;
import java.util.Map;

/**
 * @author sj
 */
@FunctionalInterface
public interface NotificationChannel {

	void sendMessage(String type, Map<String, Object> payload) throws IOException;

	default void sendMessage(String type) throws IOException {
		sendMessage(type, Map.of());
	}

}
