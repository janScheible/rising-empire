package com.scheible.risingempire.webapp.adapter.frontend._scenario;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

		Step(int bitMask) {
			this.bitMask = bitMask;
		}

		private int getBitMask() {
			return this.bitMask;
		}

	}

	private static final Map<String, Step> MAIN_PAGE_STATE_STEP_MAPPING = Map.of(//
			"SpaceCombatSystemState", Step.SPACE_COMBAT, //
			"ExplorationState", Step.EXPLORATION, //
			"ColonizationState", Step.COLONIZATION, //
			"AnnexationState", Step.ANNEXATION, //
			"NotificationState", Step.NOTIFICATION, //
			"NewTurnState", Step.NEW_TURN);

	private static List<StepsParameter> getSteps() {
		List<StepsParameter> stepsParameters = new ArrayList<>();

		for (int i = 0b11111; i >= 0; i--) {
			List<Step> steps = new ArrayList<>();

			for (Step step : Step.values()) {
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
	void newTurn(StepsParameter stepsParameter) throws Exception {
		List<Step> steps = stepsParameter.steps;
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
				.fakeTechProvider((player, round) -> (steps.contains(Step.SELECT_TECH_PAGE) && round == 4)
						? Set.of(new TechGroupView(Arrays2.asSet( //
								new TechView(new TechId("hl"), "Hand Lasers", "Bla..."),
								new TechView(new TechId("gl"), "Gatling Laser", "Bla..."),
								new TechView(new TechId("hvr"), "Hyper-V Rockets", "Bla..."))))
						: Set.of())
				// make the notifications controllable by the test
				.fakeSystemNotificationProvider((player, round) -> steps.contains(Step.NOTIFICATION) && round == 4 ? Set
					.of(new SystemNotificationView(WHITE_HOME_SYSTEM_ID, Set.of("Home system notification...")),
							new SystemNotificationView(YELLOW_HOME_SYSTEM_ID, Set.of("Other system notification...")))
						: Set.of())));

		Game game = getGame();

		game.registerAi(Player.WHITE);
		game.registerAi(Player.YELLOW);

		doBlueTurn(game, bluePlayerGame -> {
			if (steps.contains(Step.ANNEXATION)) {
				bluePlayerGame.deployHomeFleet("Cruiser", 1, "s240x440");
				bluePlayerGame.deployHomeFleet("Cruiser", 1, "s300x140");
			}
		});

		doBlueTurn(game, bluePlayerGame -> {
		});

		TestPlayerGame bluePlayerGame = createBluePlayerGame(game);

		if (steps.contains(Step.SPACE_COMBAT) && steps.contains(Step.SPACE_COMBAT_PAGE)) {
			bluePlayerGame.deployHomeFleet("Colony Ship", 1, WHITE_HOME_SYSTEM_ID);
			bluePlayerGame.deployHomeFleet("Colony Ship", 1, YELLOW_HOME_SYSTEM_ID);
		}
		else if (!(!steps.contains(Step.SPACE_COMBAT) && !steps.contains(Step.SPACE_COMBAT_PAGE))) {
			throw new IllegalArgumentException("Either both SPACE_COMBAT and SPACE_COMBAT_COMMAND or none at all!");
		}
		if (steps.contains(Step.EXPLORATION)) {
			bluePlayerGame.deployHomeFleet("Scout", 1, "s340x140");
			bluePlayerGame.deployHomeFleet("Scout", 1, "s984x728");
		}
		if (steps.contains(Step.COLONIZATION)) {
			bluePlayerGame.deployHomeFleet("Colony Ship", 1, "s80x260");
			bluePlayerGame.deployHomeFleet("Colony Ship", 1, "s180x220");
		}

		//
		// when the whole flow is performed
		//

		List<Step> actualSteps = new ArrayList<>();

		HypermediaClient startTypeSpecificBlueClient = null;
		if (stepsParameter.startType == StartType.FINISH_TURN) {
			startTypeSpecificBlueClient = finishTurn(BLUE_HOME_SYSTEM_ID, bluePlayerGame.getRound());
			assertThat(startTypeSpecificBlueClient).is2xxSuccessful()
				.isNotNewTurn()
				.is(mainPageState("FleetMovementState"));

			beginNewTurn(startTypeSpecificBlueClient);
		}
		else if (stepsParameter.startType == StartType.RELOAD) {
			// turn has to be finished manually because 'finish-turn' action is not called
			bluePlayerGame.finishTurn();

			startTypeSpecificBlueClient = createHypermediaClient(Player.BLUE);
		}
		HypermediaClient blueClient = startTypeSpecificBlueClient;

		Step current = identifyStep(blueClient);

		while (current != Step.NEW_TURN) {
			actualSteps.add(current);

			if (current == Step.SPACE_COMBAT) {
				blueClient.submit("$.inspector.spaceCombat._actions[?(@.name=='continue')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn();
				actualSteps.add(current = identifyStep(blueClient));

				blueClient.submit("$._actions[?(@.name=='continue')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn().is(mainPageState("SpaceCombatSystemState"));

				blueClient.submit("$.inspector.spaceCombat._actions[?(@.name=='continue')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn();

				blueClient.submit("$._actions[?(@.name=='continue')]");
			}
			else if (current == Step.SELECT_TECH_PAGE) {
				blueClient.submit("$.techs[0]._actions[0]");
			}
			else if (current == Step.EXPLORATION) {
				blueClient.submit("$.inspector.exploration._actions[?(@.name=='continue')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn().is(mainPageState("ExplorationState"));

				blueClient.submit("$.inspector.exploration._actions[?(@.name=='continue')]");
			}
			else if (current == Step.COLONIZATION) {
				blueClient.submit("$.inspector.colonization._actions[?(@.name=='cancel')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn().is(mainPageState("ColonizationState"));

				blueClient.submit("$.inspector.colonization._actions[?(@.name=='colonize')]");
			}
			else if (current == Step.ANNEXATION) {
				blueClient.submit("$.inspector.annexation._actions[?(@.name=='cancel')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn().is(mainPageState("AnnexationState"));

				blueClient.submit("$.inspector.annexation._actions[?(@.name=='annex')]");
			}
			else if (current == Step.NOTIFICATION) {
				blueClient.submit("$.starMap.starNotification._actions[?(@.name=='confirm')]");
				assertThat(blueClient).is2xxSuccessful().isNotNewTurn().is(mainPageState("NotificationState"));

				blueClient.submit("$.starMap.starNotification._actions[?(@.name=='confirm')]");
			}

			assertThat(blueClient).is2xxSuccessful();
			boolean lastStep = steps.indexOf(current) == steps.size() - 1;
			if (!lastStep) {
				assertThat(blueClient).isNotNewTurn();
			}

			current = identifyStep(blueClient);
		}

		Map<String, Integer> starSelection = JsonPath.parse(blueClient.getPageContentAsString())
			.read("$.starMap.starSelection");

		//
		// then every expected step is executed
		//
		assertThat(blueClient).is2xxSuccessful().isNewTurn().is(mainPageState("NewTurnState"));
		assertThat(starSelection).isEqualTo(Map.of("x", bluePlayerGame.getHomeSystemLocation().getX(), "y",
				bluePlayerGame.getHomeSystemLocation().getY()));
		assertThat(actualSteps).isEqualTo(steps);
	}

	Step identifyStep(HypermediaClient hypermediaClient) throws UnsupportedEncodingException {
		ParseContext parseContext = JsonPath.using(Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build());
		DocumentContext documentContext = parseContext.parse(hypermediaClient.getPageContentAsString());

		String type = documentContext.read("$.@type");
		if ("SelectTechPageDto".equals(type)) {
			return Step.SELECT_TECH_PAGE;
		}
		else if ("SpaceCombatPageDto".equals(type)) {
			return Step.SPACE_COMBAT_PAGE;
		}
		else if ("MainPageDto".equals(type)) {
			String stateName = documentContext.read("$.stateDescription.stateName");

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

	private static void doBlueTurn(Game game, Consumer<TestPlayerGame> turnActions) {
		TestPlayerGame bluePlayerGame = createBluePlayerGame(game);
		turnActions.accept(bluePlayerGame);
		bluePlayerGame.finishTurn();
	}

	private static TestPlayerGame createBluePlayerGame(Game game) {
		PlayerGame playerGame = game.forPlayer(Player.BLUE);
		GameView gameView = playerGame.getView();
		SystemView homeSystem = gameView.getHomeSystem();
		FleetView homeFleet = gameView.getFleets()
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

}
