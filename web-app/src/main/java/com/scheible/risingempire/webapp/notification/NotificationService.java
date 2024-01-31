package com.scheible.risingempire.webapp.notification;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.scheible.risingempire.game.api.universe.Player;
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

	public NotificationService(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void registerBroadcastChannel(String channelId, NotificationChannel channel) {
		this.broadcastChannels.put(channelId, channel);
	}

	/**
	 * @return Return {@code true} if the channel can be sucessfully registered.
	 */
	public boolean registerChannel(String gameId, Player player, String gameSessionId, NotificationChannel channel)
			throws IOException {
		String registeredGameSessionId = this.playerSessionIds.computeIfAbsent(gameId, key -> new ConcurrentHashMap<>())
			.get(player);

		if (registeredGameSessionId == null || registeredGameSessionId.equals(gameSessionId)) {
			NotificationChannel existingChannel = this.notificationChannels
				.computeIfAbsent(gameId, key -> new ConcurrentHashMap<>())
				.put(player, channel);
			if (existingChannel == null) {
				logger.info("Added notification channel for '{}' of gameId '{}'.", player, gameId);

				this.playerSessionIds.computeIfAbsent(gameId, key -> new ConcurrentHashMap<>())
					.put(player, gameSessionId);

				this.eventPublisher.publishEvent(new ChannelAddedEvent(this, player, gameId));

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

	public boolean hasChannel(String gameId, Player player) {
		return this.notificationChannels.getOrDefault(gameId, Map.of()).get(player) != null;
	}

	public boolean send(String gameId, Player player, String type, Map<String, Object> payload) {
		NotificationChannel channel = this.notificationChannels.getOrDefault(gameId, Map.of()).get(player);
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

	public boolean send(String gameId, Player player, String type) {
		return send(gameId, player, type, Map.of());
	}

	public void broadcast(String type) {
		for (NotificationChannel broadcastChannel : this.broadcastChannels.values()) {
			try {
				broadcastChannel.sendMessage(type);
			}
			catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		}
	}

	public Optional<String> getPlayerSession(String gameId, Player player) {
		return Optional.ofNullable(this.playerSessionIds.getOrDefault(gameId, Map.of()).get(player));
	}

	public void removePlayerSession(String gameId, Player player) {
		this.playerSessionIds.getOrDefault(gameId, Map.of()).remove(player);
	}

	public void unregisterBroadcastChannel(String channelId) {
		this.broadcastChannels.remove(channelId);
	}

	public void unregisterChannel(String gameId, Player player) {
		this.notificationChannels.getOrDefault(gameId, Map.of()).remove(player);
		logger.info("Removed notification channel for '{}' of gameId '{}'.", player, gameId);

		broadcast("game-change");
	}

}
