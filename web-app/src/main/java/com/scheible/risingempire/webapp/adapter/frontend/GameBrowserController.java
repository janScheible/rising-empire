package com.scheible.risingempire.webapp.adapter.frontend;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp.adapter.frontend.GameBrowserDto.GameLauncherDto;
import com.scheible.risingempire.webapp.adapter.frontend.GameBrowserDto.RunningGameDto;
import com.scheible.risingempire.webapp.adapter.frontend.GameBrowserDto.RunningGameDto.RunningGamePlayerDto;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.adapter.frontend.dto.PlayerDto;
import com.scheible.risingempire.webapp.game.GameHolder;
import com.scheible.risingempire.webapp.game.GameManager;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;
import com.scheible.risingempire.webapp.notification.NotificationService;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
@RestController
@RequestMapping(produces = APPLICATION_JSON_VALUE)
class GameBrowserController {

	private static List<String> SPACE_WORDS = List.of("Space", "Explore", "Star", "Ship", "Fleet", "Planet", "Colony",
			"Galaxy", "Combat", "Universe", "System", "Technology", "Research", "Trade");

	private final GameHolder gameHolder;
	private final GameManager gameManager;
	private final NotificationService notificationService;

	GameBrowserController(final GameHolder gameHolder, final GameManager gameManager,
			final NotificationService notificationService) {
		this.gameHolder = gameHolder;
		this.gameManager = gameManager;
		this.notificationService = notificationService;
	}

	@GetMapping("/frontend/game-browser")
	@SuppressFBWarnings(value = "PREDICTABLE_RANDOM", justification = "Random enough for game ids.")
	ResponseEntity<GameBrowserDto> gameBrowser() {
		final String defaultGameId = ThreadLocalRandom.current().ints(0, SPACE_WORDS.size()).distinct().limit(3)
				.mapToObj(i -> SPACE_WORDS.get(i)).collect(Collectors.joining());

		return ResponseEntity.ok(new GameBrowserDto(
				new GameLauncherDto(defaultGameId, List.of(PlayerDto.YELLOW, PlayerDto.BLUE, PlayerDto.WHITE)),
				gameHolder.getGameIds().stream()
						.map(gameId -> new EntityModel<>(new RunningGameDto(gameId, toRunningGamePlayers(gameId)))
								.with(Action.delete("stop", "frontend", gameId)))
						.toList()));
	}

	private List<EntityModel<RunningGamePlayerDto>> toRunningGamePlayers(final String gameId) {
		final List<EntityModel<RunningGamePlayerDto>> result = new ArrayList<>();

		final Game game = gameHolder.get(gameId).get();

		for (final Player player : game.getPlayers()) {
			final boolean canReceiveNotifications = notificationService.hasChannel(gameId, player);
			result.add(new EntityModel<>(
					new RunningGamePlayerDto(PlayerDto.fromPlayer(player), !game.isAiControlled(player),
							notificationService.getPlayerSession(gameId, player).orElse(null), canReceiveNotifications))
									.with(!game.isAiControlled(player) && !canReceiveNotifications,
											() -> Action.delete("kick", "frontend", gameId,
													player.name().toLowerCase()))
									.with(game.isAiControlled(player),
											() -> Action.get("join", "frontend", gameId, player.name().toLowerCase())
													.with("gameId", gameId)
													.with("player", player.name().toLowerCase())));
		}

		return result;
	}

	@DeleteMapping(path = "/frontend/{gameId:^(?!\\bnotifications\\b).*$}/{player:^\\w+$}")
	ResponseEntity<Object> kickPlayer(@ModelAttribute final FrontendContext context) {
		gameManager.kickPlayer(context.getGameId(), context.getPlayer());
		return ResponseEntity.ok(new Object());
	}

	@DeleteMapping(path = "/frontend/{gameId:^(?!\\bnotifications\\b).*$}")
	ResponseEntity<Object> stopGame(@ModelAttribute final FrontendContext context) {
		gameManager.stopGame(context.getGameId());
		return ResponseEntity.ok(new Object());
	}
}
