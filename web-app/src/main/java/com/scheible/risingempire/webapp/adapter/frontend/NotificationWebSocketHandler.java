package com.scheible.risingempire.webapp.adapter.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp.notification.NotificationChannel;
import com.scheible.risingempire.webapp.notification.NotificationService;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class NotificationWebSocketHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketHandler.class);

	private final ObjectMapper objectMapper;
	private final NotificationService notificationService;

	/**
	 * A particular WebSocket session can be rejected because another player session is already active for a game
	 * and player.
	 */
	private final Set<String> rejectedWebSockerSessionIds = new ConcurrentSkipListSet<>();

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The objects are not mutated.")
	public NotificationWebSocketHandler(final ObjectMapper objectMapper,
			final NotificationService notificationService) {
		this.objectMapper = objectMapper;
		this.notificationService = notificationService;
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
		final WebSocketSession concurrentSession = new ConcurrentWebSocketSessionDecorator(session, 1000, 1024);
		final NotificationChannel notificationChannel = new WebSocketNotification(concurrentSession, objectMapper);

		final SessionContext sessionContext = getFromSession(session);
		if (sessionContext != null) {
			if (!notificationService.registerChannel(sessionContext.gameId(), sessionContext.player(),
					sessionContext.playerSessionId(), notificationChannel)) {
				rejectedWebSockerSessionIds.add(session.getId());
			}
		} else {
			notificationService.registerBroadcastChannel(session.getId(), notificationChannel);
		}
	}

	@Override
	public void handleTransportError(final WebSocketSession session, final Throwable exception) throws Exception {
		final SessionContext sessionContext = getFromSession(session);
		if (sessionContext != null) {
			logger.warn("WebSocket transport error for '{}' ({}) of gameId '{}'.", sessionContext.player(),
					sessionContext.playerSessionId(), sessionContext.gameId(), exception);
		} else {
			logger.warn("WebSocket transport error.", exception);
		}
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
		if (!rejectedWebSockerSessionIds.contains(session.getId())) {
			final SessionContext sessionContext = getFromSession(session);
			if (sessionContext != null) {
				notificationService.unregisterChannel(sessionContext.gameId(), sessionContext.player());
			} else {
				notificationService.unregisterBroadcastChannel(session.getId());
			}
		} else {
			rejectedWebSockerSessionIds.remove(session.getId());
		}
	}

	@SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "No idea what else than a NullPointerException can be the result.")
	private static SessionContext getFromSession(final WebSocketSession session) {
		final MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(session.getUri()).build()
				.getQueryParams();
		final String gameId = queryParams.getFirst("gameId");
		final String playerString = queryParams.getFirst("player");
		final String sessionId = queryParams.getFirst("sessionId");

		if (gameId != null && playerString != null && sessionId != null) {
			final Player player = Player.valueOf(playerString);
			return new SessionContext(gameId, player, sessionId);
		} else {
			return null;
		}
	}

	private record SessionContext(String gameId, Player player, String playerSessionId) {

	}

	private static class WebSocketNotification implements NotificationChannel {

		private final WebSocketSession webSocketSession;
		private final ObjectMapper objectMapper;

		public WebSocketNotification(final WebSocketSession webSocketSession, final ObjectMapper objectMapper) {
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
}
