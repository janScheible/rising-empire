package com.scheible.risingempire.webapp.adapter.frontend._scenario;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.api.view.tech.TechView;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.util.jdk.Arrays2;
import com.scheible.risingempire.webapp._hypermedia.HypermediaClient;
import com.scheible.risingempire.webapp.adapter.frontend._scenario.NewTurnStepsIT.StepsParameter.StartType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.JsonAssertCondition.mainPageState;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.MainPageAssert.assertThat;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.NewTurnStepsIT.Step.ANNEXATION;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.NewTurnStepsIT.Step.COLONIZATION;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.NewTurnStepsIT.Step.EXPLORATION;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.NewTurnStepsIT.Step.NEW_TURN;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.NewTurnStepsIT.Step.NOTIFICATION;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.NewTurnStepsIT.Step.SELECT_TECH_PAGE;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.NewTurnStepsIT.Step.SPACE_COMBAT;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.NewTurnStepsIT.Step.SPACE_COMBAT_PAGE;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.NewTurnStepsIT.StepsParameter.StartType.FINISH_TURN;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.NewTurnStepsIT.StepsParameter.StartType.RELOAD;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class NewTurnStepsIT extends AbstractMainPageIT {

	private static final Logger logger = LoggerFactory.getLogger(NewTurnStepsIT.class);

	enum Step {

		SPACE_COMBAT(0b100000), SPACE_COMBAT_PAGE(0b100000), SELECT_TECH_PAGE(0b010000), EXPLORATION(0b001000),
		COLONIZATION(0b000100), ANNEXATION(0b000010), NOTIFICATION(0b000001), NEW_TURN(0);

		final int bitMask;

		private Step(int bitMask) {
			this.bitMask = bitMask;
		}

		private int getBitMask() {
			return bitMask;
		}

	}

	static class StepsParameter {

		enum StartType {

			FINISH_TURN, RELOAD

		}

		final List<Step> steps;

		final StartType startType;

		StepsParameter(List<Step> steps, StartType startType) {
			this.steps = steps;
			this.startType = startType;
		}

	}

	private interface TestPlayerGame {

		void deployHomeFleet(String shipType, int count, String destinationSystemId);

		void deployHomeFleet(String shipType, int count, SystemId destinationId);

		Location getHomeSystemLocation();

		int getRound();

		void finishTurn();

	}

	@FunctionalInterface
	private interface TurnActions {

		void turn(TestPlayerGame bluePlayerGame, TestPlayerGame yellowPlayerGame, TestPlayerGame whitePlayerGame);

	}

	private static final Map<String, Step> MAIN_PAGE_STATE_STEP_MAPPING = Map.of(//
			"SpaceCombatSystemState", SPACE_COMBAT, //
			"ExplorationState", EXPLORATION, //
			"ColonizationState", COLONIZATION, //
			"AnnexationState", ANNEXATION, //
			"NotificationState", NOTIFICATION, //
			"NewTurnState", NEW_TURN);

	private static List<StepsParameter> getSteps() {
		final List<StepsParameter> stepsParameters = new ArrayList<>();

		for (int i = 0b11111; i >= 0; i--) {
			final List<Step> steps = new ArrayList<>();

			for (final Step step : Step.values()) {
				if (step.getBitMask() > 0 && (i & step.getBitMask()) == step.getBitMask()) {
					steps.add(step);
				}
			}

			Stream.of(StartType.values()).forEach(st -> stepsParameters.add(new StepsParameter(steps, st)));
		}

		return stepsParameters;
	}

	@ParameterizedTest
	@MethodSource("getSteps")
	void newTurn(final StepsParameter stepsParameter) throws Exception {
		final List<Step> steps = stepsParameter.steps;
		logger.info("Steps: {} with {}", steps, stepsParameter.startType);

		//
		// given
		//

		startGameForBlue(GameFactory.get()
			.create(GameOptions.forTestGameScenario()
				// make the whole map reachable in a single turn for a simpler test setup
				.fleetRangeFactor(2000.0)
				.fleetSpeedFactor(2000.0)
				// decrease the number of turns of siege required to annex a system to 1
				.annexationSiegeRounds(1)
				// make a technology selectable in round 4 (but only if select tech step
				// is included)
				.fakeTechProvider((player, round) -> (steps.contains(SELECT_TECH_PAGE) && round == 4)
						? Collections.singleton(new TechGroupView(Arrays2.asSet( //
								new TechView(new TechId("hl"), "Hand Lasers", "Bla..."),
								new TechView(new TechId("gl"), "Gatling Laser", "Bla..."),
								new TechView(new TechId("hvr"), "Hyper-V Rockets", "Bla..."))))
						: emptySet())
				// make the notifications controllable by the test
				.fakeSystemNotificationProvider((player, round) -> steps.contains(NOTIFICATION) && round == 4 ? Set.of(
						new SystemNotificationView(WHITE_HOME_SYSTEM_ID, Set.of("Home system notification...")),
						new SystemNotificationView(YELLOW_HOME_SYSTEM_ID, Set.of("Other system notification...")))
						: Set.of())));

		final Game game = getGame();

		game.registerAi(Player.WHITE);
		game.registerAi(Player.YELLOW);

		doBlueTurn(game, bluePlayerGame -> {
			if (steps.contains(ANNEXATION)) {
				bluePlayerGame.deployHomeFleet("Cruiser", 1, "s240x440");
				bluePlayerGame.deployHomeFleet("Cruiser", 1, "s300x140");
			}
		});

		doBlueTurn(game, bluePlayerGame -> {
		});

		final TestPlayerGame bluePlayerGame = createBluePlayerGame(game);

		if (steps.contains(SPACE_COMBAT) && steps.contains(SPACE_COMBAT_PAGE)) {
			bluePlayerGame.deployHomeFleet("Colony Ship", 1, WHITE_HOME_SYSTEM_ID);
			bluePlayerGame.deployHomeFleet("Colony Ship", 1, YELLOW_HOME_SYSTEM_ID);
		}
		else if (!(!steps.contains(SPACE_COMBAT) && !steps.contains(SPACE_COMBAT_PAGE))) {
			throw new IllegalArgumentException("Either both SPACE_COMBAT and SPACE_COMBAT_COMMAND or none at all!");
		}
		if (steps.contains(EXPLORATION)) {
			bluePlayerGame.deployHomeFleet("Scout", 1, "s340x140");
			bluePlayerGame.deployHomeFleet("Scout", 1, "s984x728");
		}
		if (steps.contains(COLONIZATION)) {
			bluePlayerGame.deployHomeFleet("Colony Ship", 1, "s80x260");
			bluePlayerGame.deployHomeFleet("Colony Ship", 1, "s180x220");
		}

		//
		// when the whole flow is performed
		//

		final List<Step> actualSteps = new ArrayList<>();

		HypermediaClient startTypeSpecificBlueClient = null;
		if (stepsParameter.startType == FINISH_TURN) {
			startTypeSpecificBlueClient = finishTurn(BLUE_HOME_SYSTEM_ID, bluePlayerGame.getRound());
			assertThat(startTypeSpecificBlueClient).is2xxSuccessful()
				.isNotNewTurn()
				.is(mainPageState("FleetMovementState"));

			beginNewTurn(startTypeSpecificBlueClient);
		}
		else if (stepsParameter.startType == RELOAD) {
			// turn has to be finished manually because 'finish-turn' action is not called
			bluePlayerGame.finishTurn();

			startTypeSpecificBlueClient = createHypermediaClient(Player.BLUE);
		}
		final HypermediaClient blueClient = startTypeSpecificBlueClient;

		Step current = identifyStep(blueClient);

		while (current != NEW_TURN) {
			actualSteps.add(current);

			if (current == SPACE_COMBAT) {
				blueClient.submit("$.inspector.spaceCombat._actions[?(@.name=='continue')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn();
				actualSteps.add(current = identifyStep(blueClient));

				blueClient.submit("$._actions[?(@.name=='continue')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn().is(mainPageState("SpaceCombatSystemState"));

				blueClient.submit("$.inspector.spaceCombat._actions[?(@.name=='continue')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn();

				blueClient.submit("$._actions[?(@.name=='continue')]");
			}
			else if (current == SELECT_TECH_PAGE) {
				blueClient.submit("$.techs[0]._actions[0]");
			}
			else if (current == EXPLORATION) {
				blueClient.submit("$.inspector.exploration._actions[?(@.name=='continue')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn().is(mainPageState("ExplorationState"));

				blueClient.submit("$.inspector.exploration._actions[?(@.name=='continue')]");
			}
			else if (current == COLONIZATION) {
				blueClient.submit("$.inspector.colonization._actions[?(@.name=='cancel')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn().is(mainPageState("ColonizationState"));

				blueClient.submit("$.inspector.colonization._actions[?(@.name=='colonize')]");
			}
			else if (current == ANNEXATION) {
				blueClient.submit("$.inspector.annexation._actions[?(@.name=='cancel')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn().is(mainPageState("AnnexationState"));

				blueClient.submit("$.inspector.annexation._actions[?(@.name=='annex')]");
			}
			else if (current == NOTIFICATION) {
				blueClient.submit("$.starMap.starNotification._actions[?(@.name=='confirm')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn().is(mainPageState("NotificationState"));

				blueClient.submit("$.starMap.starNotification._actions[?(@.name=='confirm')]");
			}

			assertThat(blueClient).is2xxSuccessful();
			final boolean lastStep = steps.indexOf(current) == steps.size() - 1;
			if (!lastStep) {
				assertThat(blueClient).isNotNewTurn();
			}

			current = identifyStep(blueClient);
		}

		final Map<String, Integer> starSelection = JsonPath.parse(blueClient.getPageContentAsString())
			.read("$.starMap.starSelection");

		//
		// then every expected step is executed
		//
		assertThat(blueClient).is2xxSuccessful().isNewTurn().is(mainPageState("NewTurnState"));
		assertThat(starSelection).isEqualTo(Map.of("x", bluePlayerGame.getHomeSystemLocation().getX(), "y",
				bluePlayerGame.getHomeSystemLocation().getY()));
		assertThat(actualSteps).isEqualTo(steps);
	}

	Step identifyStep(final HypermediaClient hypermediaClient) throws UnsupportedEncodingException {
		final ParseContext parseContext = JsonPath
			.using(Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build());
		final DocumentContext documentContext = parseContext.parse(hypermediaClient.getPageContentAsString());

		final String type = documentContext.read("$.@type");
		if ("SelectTechPageDto".equals(type)) {
			return SELECT_TECH_PAGE;
		}
		else if ("SpaceCombatPageDto".equals(type)) {
			return SPACE_COMBAT_PAGE;
		}
		else if ("MainPageDto".equals(type)) {
			final String stateName = documentContext.read("$.stateDescription.stateName");

			if (MAIN_PAGE_STATE_STEP_MAPPING.containsKey(stateName)) {
				return MAIN_PAGE_STATE_STEP_MAPPING.get(stateName);
			}
			else {
				throw new IllegalStateException("Unknown main page state '" + stateName + "'!");
			}
		}
		else {
			throw new IllegalStateException("Unknown page type '" + type + "'!");
		}
	}

	private static void doBlueTurn(final Game game, final Consumer<TestPlayerGame> turnActions) {
		final TestPlayerGame bluePlayerGame = createBluePlayerGame(game);
		turnActions.accept(bluePlayerGame);
		bluePlayerGame.finishTurn();
	}

	private static TestPlayerGame createBluePlayerGame(final Game game) {
		final PlayerGame playerGame = game.forPlayer(Player.BLUE);
		final GameView gameView = playerGame.getView();
		final SystemView homeSystem = gameView.getHomeSystem();
		final FleetView homeFleet = gameView.getFleets()
			.stream()
			.filter(f -> homeSystem.getId().equals(f.getOrbiting().orElse(null)))
			.findFirst()
			.orElseThrow();

		return new TestPlayerGame() {
			@Override
			public void deployHomeFleet(String shipType, int count, String destinationSystemId) {
				deployHomeFleet(shipType, count, new SystemId(destinationSystemId));
			}

			@Override
			public void deployHomeFleet(String shipType, int count, SystemId destinationId) {
				playerGame.deployFleet(homeFleet.getId(), destinationId,
						Map.of(homeFleet.getShipType(shipType).getId(), count));
			}

			@Override
			public Location getHomeSystemLocation() {
				return gameView.getHomeSystem().getLocation();
			}

			@Override
			public int getRound() {
				return gameView.getRound();
			}

			@Override
			public void finishTurn() {
				playerGame.finishTurn();
			}
		};
	}

}
