package com.scheible.risingempire.webapp.game;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.TurnStatus;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp.adapter.frontend.dto.TurnFinishedStatusPlayerDto;
import com.scheible.risingempire.webapp.notification.ChannelAddedEvent;
import com.scheible.risingempire.webapp.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author sj
 */
@Component
public class GameManager {

	private static final Logger logger = LoggerFactory.getLogger(GameManager.class);

	private final GameHolder gameHolder;

	private final NotificationService notificationService;

	public GameManager(GameHolder gameHolder, NotificationService notificationService) {
		this.gameHolder = gameHolder;
		this.notificationService = notificationService;
	}

	public void startGame(String gameId, Player startingPlayer, Game game) {
		game.getPlayers()
			.stream()
			.filter(player -> player != startingPlayer)
			.forEach(otherPlayer -> game.registerAi(otherPlayer));

		this.gameHolder.set(gameId, game);

		this.notificationService.broadcast("game-change");
	}

	public void turnFinished(String gameId, Player player, TurnStatus turnStatus) {
		if (turnStatus.roundFinished()) {
			turnStatus.playerStatus()
				.keySet()
				.stream()
				.filter(c -> c != player)
				.forEach(p -> this.notificationService.send(gameId, p, "turn-finished"));

			this.notificationService.broadcast("game-change");
		}
		else {
			for (Entry<Player, Boolean> playerTurnStatus : turnStatus.playerStatus().entrySet()) {
				if (playerTurnStatus.getValue() && playerTurnStatus.getKey() != player) {
					this.notificationService.send(gameId, playerTurnStatus.getKey(), "turn-finish-status",
							Map.of("playerStatus", turnStatus.playerStatus()
								.entrySet()
								.stream()
								.map(cts -> TurnFinishedStatusPlayerDto.fromPlayer(cts.getKey(), cts.getValue()))
								.collect(Collectors.toList())));
				}
			}
		}
	}

	@EventListener
	public void registerPlayer(ChannelAddedEvent addedEvent) {
		this.gameHolder.get(addedEvent.getGameId()).ifPresent(game -> game.unregisterAi(addedEvent.getPlayer()));
	}

	public void kickPlayer(String gameId, Player player) {
		kickPlayer(gameId, player, "player-kicked");
	}

	private void kickPlayer(String gameId, Player player, String eventType) {
		Game game = this.gameHolder.get(gameId).get();
		game.registerAi(player);
		turnFinished(gameId, player, game.forPlayer(player).finishTurn());

		this.notificationService.removePlayerSession(gameId, player);
		logger.info("Kicked '{}' of gameId '{}' with {}.", player, gameId, eventType);

		if (this.notificationService.send(gameId, player, eventType)) {
			this.notificationService.unregisterChannel(gameId, player);
		}

		this.notificationService.broadcast("game-change");
	}

	public void stopGame(String gameId) {
		this.gameHolder.get(gameId)
			.get()
			.getPlayers()
			.stream()
			.filter(player -> this.notificationService.hasChannel(gameId, player))
			.forEach(player -> kickPlayer(gameId, player, "game-stopped"));
		this.gameHolder.removeGame(gameId);

		this.notificationService.broadcast("game-change");
	}

}
