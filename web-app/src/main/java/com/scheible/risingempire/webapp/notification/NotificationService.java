package com.scheible.risingempire.webapp.notification;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.scheible.risingempire.game.api.view.universe.Player;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author sj
 */
@Service
public class NotificationService {

	public enum ErrorCause {

		SEND, GENERIC

	}

	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	private final Map<String, NotificationChannel> broadcastChannels = new ConcurrentHashMap<>();

	private final Map<String, Map<Player, NotificationChannel>> notificationChannels = new ConcurrentHashMap<>();

	private final Map<String, Map<Player, String>> playerSessionIds = new ConcurrentHashMap<>();

	private final ApplicationEventPublisher eventPublisher;

	public NotificationService(final ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void registerBroadcastChannel(final String channelId, final NotificationChannel channel) {
		broadcastChannels.put(channelId, channel);
	}

	/**
	 * @return Return {@code true} if the channel can be sucessfully registered.
	 */
	public boolean registerChannel(final String gameId, final Player player, final String gameSessionId,
			final NotificationChannel channel) throws IOException {
		final String registeredGameSessionId = playerSessionIds
			.computeIfAbsent(gameId, key -> new ConcurrentHashMap<>())
			.get(player);

		if (registeredGameSessionId == null || registeredGameSessionId.equals(gameSessionId)) {
			final NotificationChannel existingChannel = notificationChannels
				.computeIfAbsent(gameId, key -> new ConcurrentHashMap<>())
				.put(player, channel);
			if (existingChannel == null) {
				logger.info("Added notification channel for '{}' of gameId '{}'.", player, gameId);

				playerSessionIds.computeIfAbsent(gameId, key -> new ConcurrentHashMap<>()).put(player, gameSessionId);

				eventPublisher.publishEvent(new ChannelAddedEvent(this, player, gameId));

				channel.sendMessage("player-available");

				broadcast("game-change");
				return true;
			}
			else {
				logger.info("There is already a notification channel for '{}' of gameId '{}'.", player, gameId);
			}
		}
		else {
			logger.info("Notification channel for '{}' of gameId '{}' was already used.", player, gameId);
		}

		channel.sendMessage("player-already-taken");
		return false;
	}

	public boolean hasChannel(final String gameId, final Player player) {
		return notificationChannels.getOrDefault(gameId, Collections.emptyMap()).get(player) != null;
	}

	@SuppressFBWarnings(value = { "EXS_EXCEPTION_SOFTENING_RETURN_FALSE", "EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS" },
			justification = "Notification channel is removed and exception is logged.")
	public final boolean send(final String gameId, final Player player, final String type,
			final Map<String, Object> payload) {
		final NotificationChannel channel = notificationChannels.getOrDefault(gameId, Collections.emptyMap())
			.get(player);
		if (channel != null) {
			try {
				channel.sendMessage(type, payload);
			}
			catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}

			return true;
		}
		else {
			return false;
		}
	}

	public final boolean send(final String gameId, final Player player, final String type) {
		return send(gameId, player, type, Collections.emptyMap());
	}

	@SuppressFBWarnings(value = "EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS",
			justification = "Do not bother everybody with that IOException.")
	public final void broadcast(final String type) {
		for (final NotificationChannel broadcastChannel : broadcastChannels.values()) {
			try {
				broadcastChannel.sendMessage(type);
			}
			catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		}
	}

	public Optional<String> getPlayerSession(final String gameId, final Player player) {
		return Optional.ofNullable(playerSessionIds.getOrDefault(gameId, Collections.emptyMap()).get(player));
	}

	public void removePlayerSession(final String gameId, final Player player) {
		playerSessionIds.getOrDefault(gameId, Collections.emptyMap()).remove(player);
	}

	public void unregisterBroadcastChannel(final String channelId) {
		broadcastChannels.remove(channelId);
	}

	public void unregisterChannel(final String gameId, final Player player) {
		notificationChannels.getOrDefault(gameId, Collections.emptyMap()).remove(player);
		logger.info("Removed notification channel for '{}' of gameId '{}'.", player, gameId);

		broadcast("game-change");
	}

}
