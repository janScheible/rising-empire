package com.scheible.risingempire.webapp.adapter.frontend.newgamepage;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api._scenario.AnnexationTest;
import com.scheible.risingempire.game.api._scenario.ColonizationTest;
import com.scheible.risingempire.game.api._scenario.ExplorationTest;
import com.scheible.risingempire.game.api._scenario.SpaceCombatTest;
import com.scheible.risingempire.game.api._testgame.TestScenario;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.api.view.tech.TechView;
import com.scheible.risingempire.util.jdk.Arrays2;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.adapter.frontend.newgamepage.NewGamePageDto.ScenarioDto;
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

	private static final Map<String, AvailableScenario> TEST_GAME_SCENARIO_MAPPING = Map.of( //
			"exploration-colonized",
			new AvailableScenario(
					"Exploration (colonized system)", () -> new ExplorationTest()::testExplorationColonizedSystem),
			"exploration-uncolonized",
			new AvailableScenario("Exploration (uncolonized system)",
					() -> new ExplorationTest()::testExplorationUncolonizedSystem),
			"colonization", new AvailableScenario("Colonization", () -> new ColonizationTest()::testColonization),
			"space-combat-attacker-won",
			new AvailableScenario(
					"Space combat (attacker won)", () -> new SpaceCombatTest()::testSpaceCombatAttackerWon),
			"space-combat-defender-won",
			new AvailableScenario(
					"Space combat (defender won)", () -> new SpaceCombatTest()::testSpaceCombatDefenderWon),
			"space-combat-attacker-retreated",
			new AvailableScenario("Space combat (attacker retreated)",
					() -> new SpaceCombatTest()::testSpaceCombatAttackerRetreated),
			"annexation", new AvailableScenario("Annexation", () -> new AnnexationTest()::testAnnexation));

	private final GameHolder gameHolder;

	private final GameManager gameManager;

	NewGameController(GameHolder gameHolder, GameManager gameManager) {
		this.gameHolder = gameHolder;
		this.gameManager = gameManager;
	}

	@GetMapping(path = "/new-game-page")
	EntityModel<NewGamePageDto> newGame(@ModelAttribute FrontendContext context) {
		return new EntityModel<>(new NewGamePageDto(Arrays.asList(GalaxySize.values()),
				isTestGameId(context.getGameId()) && context.getPlayer() == Player.BLUE
						? Optional.of(TEST_GAME_SCENARIO_MAPPING.entrySet()
							.stream()
							.map(e -> new ScenarioDto(e.getKey(), e.getValue().name()))
							.sorted(Comparator.comparing(ScenarioDto::name))
							.toList())
						: Optional.empty()))
			.with(Action.jsonPost("create", context.toFrontendUri("new-game-page", "creations")));
	}

	@PostMapping(path = "/new-game-page/creations", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> createGame(@ModelAttribute FrontendContext context, @RequestBody GameCreationBodyDto body) {
		if (this.gameHolder.get(context.getGameId()).isEmpty()) {
			Game game = null;
			TestScenario testScenario = null;

			if (isTestGameId(context.getGameId())) {
				AvailableScenario availableScenario = TEST_GAME_SCENARIO_MAPPING.get(body.scenarioId.orElse(null));
				if (context.getPlayer() == Player.BLUE && availableScenario != null) {
					testScenario = TestScenario.start(availableScenario.testMethodSupplier.get());
					game = testScenario.getGame();
				}
				else {
					game = GameFactory.get()
						.create(GameOptions.forTestGame() //
							.fakeTechProvider((player,
									round) -> (round % 5 == 0) ? Set.of(new TechGroupView(Arrays2.asSet( //
											new TechView(new TechId("hl"), "Hand Lasers", "Bla..."),
											new TechView(new TechId("gl"), "Gatling Laser", "Bla..."),
											new TechView(new TechId("hvr"), "Hyper-V Rockets", "Bla...")))) : Set.of())
							.fakeSystemNotificationProvider((player, round) -> {
								if (round % 3 == 0) {
									return (round % 6 == 0) ? Set
										.of(new SystemNotificationView(new SystemId("s60x60"), Set
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
			}
			else {
				game = GameFactory.get().create(new GameOptions(body.galaxySize, body.playerCount));
			}

			this.gameManager.startGame(context.getGameId(), context.getPlayer(), game,
					Optional.ofNullable(testScenario));
		}

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION, context.toAction(HttpMethod.GET, "main-page").toGetUri())
			.build();
	}

	private static boolean isTestGameId(String gameId) {
		String normalizedGameId = gameId.replaceAll("\\-", "").toLowerCase(Locale.ROOT);
		return "testgame".equals(normalizedGameId);
	}

	static class GameCreationBodyDto {

		GalaxySize galaxySize = GalaxySize.HUGE;

		int playerCount = 3;

		Optional<String> scenarioId = Optional.empty();

	}

	private record AvailableScenario(String name, Supplier<Consumer<TestScenario>> testMethodSupplier) {

	}

}
