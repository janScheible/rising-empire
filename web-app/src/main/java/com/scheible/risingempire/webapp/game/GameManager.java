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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The objects are not mutated.")
	public GameManager(final GameHolder gameHolder, final NotificationService notificationService) {
		this.gameHolder = gameHolder;
		this.notificationService = notificationService;
	}

	public void startGame(final String gameId, final Player startingPlayer, final Game game) {
		game.getPlayers()
			.stream()
			.filter(player -> player != startingPlayer)
			.forEach(otherPlayer -> game.registerAi(otherPlayer));

		gameHolder.set(gameId, game);

		notificationService.broadcast("game-change");
	}

	public void turnFinished(final String gameId, final Player player, final TurnStatus turnStatus) {
		if (turnStatus.roundFinished()) {
			turnStatus.playerStatus()
				.keySet()
				.stream()
				.filter(c -> c != player)
				.forEach(p -> notificationService.send(gameId, p, "turn-finished"));

			notificationService.broadcast("game-change");
		}
		else {
			for (final Entry<Player, Boolean> playerTurnStatus : turnStatus.playerStatus().entrySet()) {
				if (playerTurnStatus.getValue() && playerTurnStatus.getKey() != player) {
					notificationService.send(gameId, playerTurnStatus.getKey(), "turn-finish-status",
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
	public void registerPlayer(final ChannelAddedEvent addedEvent) {
		gameHolder.get(addedEvent.getGameId()).ifPresent(game -> game.unregisterAi(addedEvent.getPlayer()));
	}

	public void kickPlayer(final String gameId, final Player player) {
		kickPlayer(gameId, player, "player-kicked");
	}

	private void kickPlayer(final String gameId, final Player player, final String eventType) {
		final Game game = gameHolder.get(gameId).get();
		game.registerAi(player);
		turnFinished(gameId, player, game.forPlayer(player).finishTurn());

		notificationService.removePlayerSession(gameId, player);
		logger.info("Kicked '{}' of gameId '{}' with {}.", player, gameId, eventType);

		if (notificationService.send(gameId, player, eventType)) {
			notificationService.unregisterChannel(gameId, player);
		}

		notificationService.broadcast("game-change");
	}

	public void stopGame(final String gameId) {
		gameHolder.get(gameId)
			.get()
			.getPlayers()
			.stream()
			.filter(player -> notificationService.hasChannel(gameId, player))
			.forEach(player -> kickPlayer(gameId, player, "game-stopped"));
		gameHolder.removeGame(gameId);

		notificationService.broadcast("game-change");
	}

}
