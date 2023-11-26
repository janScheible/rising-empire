package com.scheible.risingempire.webapp.adapter.frontend.newgamepage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.api.view.tech.TechView;
import com.scheible.risingempire.util.jdk.Arrays2;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.game.GameHolder;
import com.scheible.risingempire.webapp.game.GameManager;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author sj
 */
@FrontendController
class NewGameController {

	private final GameHolder gameHolder;

	private final GameManager gameManager;

	NewGameController(GameHolder gameHolder, GameManager gameManager) {
		this.gameHolder = gameHolder;
		this.gameManager = gameManager;
	}

	@GetMapping(path = "/new-game-page")
	EntityModel<NewGamePageDto> newGame(@ModelAttribute FrontendContext context) {
		return new EntityModel<>(new NewGamePageDto(Arrays.asList(GalaxySize.values())))
			.with(Action.jsonPost("create", context.toFrontendUri("new-game-page", "creations")) //
				.with(isTestScenarioGameId(context.getGameId()), "auto-create", () -> Boolean.TRUE));
	}

	@PostMapping(path = "/new-game-page/creations", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> createGame(@ModelAttribute FrontendContext context, @RequestBody GameCreationBodyDto body) {
		if (this.gameHolder.get(context.getGameId()).isEmpty()) {
			Game game;

			if (isTestScenarioGameId(context.getGameId())) {
				game = GameFactory.get()
					.create(GameOptions.forTestGameScenario() //
						.fakeTechProvider((player, round) -> (round % 5 == 0) ? Set.of(new TechGroupView(Arrays2.asSet( //
								new TechView(new TechId("hl"), "Hand Lasers", "Bla..."),
								new TechView(new TechId("gl"), "Gatling Laser", "Bla..."),
								new TechView(new TechId("hvr"), "Hyper-V Rockets", "Bla...")))) : Set.of())
						.fakeSystemNotificationProvider((player, round) -> {
							if (round % 3 == 0) {
								return (round % 6 == 0) ? Set.of(new SystemNotificationView(new SystemId("s60x60"), Set
									.of("This is a notification for s60x60. Please do what ever it tells you...")))
										: new HashSet<>(Arrays.asList(new SystemNotificationView(
												new SystemId("s984x728"),
												Set.of("This is a notification for s984x728. Please do what ever it tells you...")),
												new SystemNotificationView(new SystemId("s60x60"), Set
													.of("This is a notification for s60x60. Please do what ever it tells you..."))));
							}
							else {
								return Set.of();
							}
						}));
			}
			else {
				game = GameFactory.get().create(new GameOptions(body.galaxySize, body.playerCount));
			}

			this.gameManager.startGame(context.getGameId(), context.getPlayer(), game);
		}

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION, context.toAction(HttpMethod.GET, "main-page").toGetUri())
			.build();
	}

	private static boolean isTestScenarioGameId(String gameId) {
		String normalizedGameId = gameId.replaceAll("\\-", "").toLowerCase(Locale.ROOT);
		return "testscenario".equals(normalizedGameId);
	}

	static class GameCreationBodyDto {

		GalaxySize galaxySize = GalaxySize.HUGE;

		int playerCount = 3;

	}

}
