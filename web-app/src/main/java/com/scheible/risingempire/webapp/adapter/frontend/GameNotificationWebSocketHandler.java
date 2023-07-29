package com.scheible.risingempire.webapp.adapter.frontend;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
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
public class GameNotificationWebSocketHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(GameNotificationWebSocketHandler.class);

	private final ObjectMapper objectMapper;
	private final NotificationService notificationService;

	/**
	 * A particular WebSocket session can be rejected because another player session is already active for a game
	 * and player.
	 */
	private final Set<String> rejectedWebSockerSessionIds = new ConcurrentSkipListSet<>();

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The objects are not mutated.")
	public GameNotificationWebSocketHandler(final ObjectMapper objectMapper,
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
			logger.warn("Unable to get session context from '{}'.", session.getUri());
		}
	}

	@Override
	public void handleTransportError(final WebSocketSession session, final Throwable exception) throws Exception {
		final SessionContext sessionContext = getFromSession(session);
		if (sessionContext != null) {
			logger.warn("WebSocket transport error for '{}' ({}) of gameId '{}'.", sessionContext.player(),
					sessionContext.playerSessionId(), sessionContext.gameId(), exception);
		} else {
			logger.warn("WebSocket transport error for '{}'.", session.getUri(), exception);
		}
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
		if (!rejectedWebSockerSessionIds.contains(session.getId())) {
			final SessionContext sessionContext = getFromSession(session);
			if (sessionContext != null) {
				notificationService.unregisterChannel(sessionContext.gameId(), sessionContext.player());
			} else {
				logger.warn("Unable to get session context from '{}'.", session.getUri());
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
			final Player player = Player.valueOf(URLDecoder.decode(playerString, StandardCharsets.UTF_8));
			return new SessionContext(URLDecoder.decode(gameId, StandardCharsets.UTF_8), player, sessionId);
		} else {
			return null;
		}
	}

	private record SessionContext(String gameId, Player player, String playerSessionId) {

	}
}
