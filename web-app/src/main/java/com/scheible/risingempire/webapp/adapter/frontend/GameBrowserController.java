package com.scheible.risingempire.webapp.adapter.frontend;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.webapp.adapter.frontend.GameBrowserDto.GameLauncherDto;
import com.scheible.risingempire.webapp.adapter.frontend.GameBrowserDto.RunningGameDto;
import com.scheible.risingempire.webapp.adapter.frontend.GameBrowserDto.RunningGameDto.RunningGamePlayerDto;
import com.scheible.risingempire.webapp.adapter.frontend.dto.PlayerDto;
import com.scheible.risingempire.webapp.game.GameHolder;
import com.scheible.risingempire.webapp.game.GameManager;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;
import com.scheible.risingempire.webapp.notification.NotificationService;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
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

	private final Optional<GitProperties> gitProperties;

	private final Optional<BuildProperties> buildProperties;

	GameBrowserController(GameHolder gameHolder, GameManager gameManager, NotificationService notificationService,
			Optional<GitProperties> gitProperties, Optional<BuildProperties> buildProperties) {
		this.gameHolder = gameHolder;
		this.gameManager = gameManager;
		this.notificationService = notificationService;

		this.gitProperties = gitProperties;
		this.buildProperties = buildProperties;
	}

	@GetMapping("/game-browser")
	ResponseEntity<GameBrowserDto> gameBrowser() {
		String defaultGameId = ThreadLocalRandom.current()
			.ints(0, SPACE_WORDS.size())
			.distinct()
			.limit(3)
			.mapToObj(SPACE_WORDS::get)
			.collect(Collectors.joining());

		return ResponseEntity.ok(new GameBrowserDto(new EntityModel<>(
				new GameLauncherDto(defaultGameId, List.of(PlayerDto.YELLOW, PlayerDto.BLUE, PlayerDto.WHITE)))
			.with(Action.get("start", "games", "{gameId}", "{player}").with("gameId", null).with("player", null)),
				this.gameHolder.getGameIds()
					.stream()
					.map(gameId -> new EntityModel<>(new RunningGameDto(gameId, toRunningGamePlayers(gameId),
							this.gameHolder.get(gameId).get().getRound()))
						.with(Action.delete("stop", "game-browser", "games", gameId)))
					.toList(),
				this.gitProperties.map(GitProperties::getShortCommitId),
				this.buildProperties.map(BuildProperties::getTime)
					.map(ts -> DateTimeFormatter.ISO_DATE_TIME.format(ts.atOffset(ZoneOffset.UTC)))));
	}

	private List<EntityModel<RunningGamePlayerDto>> toRunningGamePlayers(String gameId) {
		List<EntityModel<RunningGamePlayerDto>> result = new ArrayList<>();

		Game game = this.gameHolder.get(gameId).get();

		for (Player player : game.getPlayers()) {
			boolean canReceiveNotifications = this.notificationService.hasChannel(gameId, player);
			result.add(new EntityModel<>(
					new RunningGamePlayerDto(PlayerDto.fromPlayer(player), !game.isAiControlled(player),
							this.notificationService.getPlayerSession(gameId, player), canReceiveNotifications))
				.with(!game.isAiControlled(player) && !canReceiveNotifications,
						() -> Action.delete("kick", "game-browser", "games", gameId,
								player.name().toLowerCase(Locale.ROOT)))
				.with(game.isAiControlled(player),
						() -> Action.get("join", "games", gameId, player.name().toLowerCase(Locale.ROOT))));
		}

		return result;
	}

	@DeleteMapping(path = "/game-browser/games/{gameId}/{player}")
	ResponseEntity<Object> kickPlayer(@PathVariable String gameId, @PathVariable Player player) {
		this.gameManager.kickPlayer(gameId, player);
		return ResponseEntity.ok(new Object());
	}

	@DeleteMapping(path = "/game-browser/games/{gameId}")
	ResponseEntity<Object> stopGame(@PathVariable String gameId) {
		this.gameManager.stopGame(gameId);
		return ResponseEntity.ok(new Object());
	}

}
