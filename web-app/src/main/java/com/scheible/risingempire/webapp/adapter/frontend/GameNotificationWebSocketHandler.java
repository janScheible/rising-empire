package com.scheible.risingempire.webapp.adapter.frontend;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp.notification.NotificationChannel;
import com.scheible.risingempire.webapp.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author sj
 */
public class GameNotificationWebSocketHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(GameNotificationWebSocketHandler.class);

	private final ObjectMapper objectMapper;

	private final NotificationService notificationService;

	/**
	 * A particular WebSocket session can be rejected because another player session is
	 * already active for a game and player.
	 */
	private final Set<String> rejectedWebSockerSessionIds = new ConcurrentSkipListSet<>();

	public GameNotificationWebSocketHandler(ObjectMapper objectMapper, NotificationService notificationService) {
		this.objectMapper = objectMapper;
		this.notificationService = notificationService;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		WebSocketSession concurrentSession = new ConcurrentWebSocketSessionDecorator(session, 1000, 1024);
		NotificationChannel notificationChannel = new WebSocketNotification(concurrentSession, this.objectMapper);

		SessionContext sessionContext = getFromSession(session);
		if (sessionContext != null) {
			if (!this.notificationService.registerChannel(sessionContext.gameId(), sessionContext.player(),
					sessionContext.playerSessionId(), notificationChannel)) {
				this.rejectedWebSockerSessionIds.add(session.getId());
			}
		}
		else {
			logger.warn("Unable to get session context from '{}'.", session.getUri());
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		SessionContext sessionContext = getFromSession(session);
		if (sessionContext != null) {
			logger.warn("WebSocket transport error for '{}' ({}) of gameId '{}'.", sessionContext.player(),
					sessionContext.playerSessionId(), sessionContext.gameId(), exception);
		}
		else {
			logger.warn("WebSocket transport error for '{}'.", session.getUri(), exception);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		if (!this.rejectedWebSockerSessionIds.contains(session.getId())) {
			SessionContext sessionContext = getFromSession(session);
			if (sessionContext != null) {
				this.notificationService.unregisterChannel(sessionContext.gameId(), sessionContext.player());
			}
			else {
				logger.warn("Unable to get session context from '{}'.", session.getUri());
			}
		}
		else {
			this.rejectedWebSockerSessionIds.remove(session.getId());
		}
	}

	private static SessionContext getFromSession(WebSocketSession session) {
		MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(session.getUri())
			.build()
			.getQueryParams();
		String gameId = queryParams.getFirst("gameId");
		String playerString = queryParams.getFirst("player");
		String sessionId = queryParams.getFirst("sessionId");

		if (gameId != null && playerString != null && sessionId != null) {
			Player player = Player.valueOf(URLDecoder.decode(playerString, StandardCharsets.UTF_8));
			return new SessionContext(URLDecoder.decode(gameId, StandardCharsets.UTF_8), player, sessionId);
		}
		else {
			return null;
		}
	}

	private record SessionContext(String gameId, Player player, String playerSessionId) {

	}

}
