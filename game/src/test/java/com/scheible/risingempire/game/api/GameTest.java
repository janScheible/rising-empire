package com.scheible.risingempire.game.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class GameTest {

	@Test
	void testExploration() {
		Game game = GameFactory.get().create(GameOptions.forTestGameScenario());
		game.registerAi(Player.WHITE);
		game.registerAi(Player.YELLOW);

		PlayerGame blueGame = game.forPlayer(Player.BLUE);
		GameView blueGameView = null;

		for (int i = 0; i < 5; i++) {
			blueGameView = blueGame.getView();

			Set<SystemId> spaceCombatSystemIds = blueGameView.getSpaceCombats()
				.stream()
				.map(SpaceCombatView::getSystemId)
				.collect(Collectors.toSet());

			if (blueGameView.getRound() == 4) {
				assertThat(spaceCombatSystemIds).containsExactly(new SystemId("s220x100"));
				assertThat(blueGameView.getJustExploredSystemIds()).isEmpty();
			}
			else if (blueGameView.getRound() == 5) {
				assertThat(blueGameView.getJustExploredSystemIds()).containsExactly(new SystemId("s180x220"));
				assertThat(spaceCombatSystemIds).isEmpty();
			}
			else {
				assertThat(blueGameView.getJustExploredSystemIds()).isEmpty();
				assertThat(spaceCombatSystemIds).isEmpty();
			}

			if (blueGameView.getRound() == 1) {
				FleetView fleetAtSol = blueGameView.getOrbiting(blueGameView.getSystem("Sol").getId()).orElseThrow();
				blueGame.deployFleet(fleetAtSol.getId(), blueGameView.getSystem(new SystemId("s220x100")).getId(),
						Map.of(fleetAtSol.getShipType("Scout").getId(), 1));
				blueGame.deployFleet(fleetAtSol.getId(), blueGameView.getSystem(new SystemId("s180x220")).getId(),
						Map.of(fleetAtSol.getShipType("Scout").getId(), 1));
			}
			else if (blueGameView.getRound() == 4) {
				GameView gameState2 = blueGameView;
				blueGameView.getOrbiting(blueGameView.getSystem("Fieras").getId())
					.ifPresent(fleetAtFieras -> blueGame.deployFleet(fleetAtFieras.getId(),
							gameState2.getSystem("Sol").getId(),
							Map.of(fleetAtFieras.getShipType("Scout").getId(), 1)));
			}

			for (TechGroupView techGroup : blueGameView.getSelectTechs()) {
				blueGame.selectTech(techGroup.iterator().next().getId());
			}

			blueGame.finishTurn();
		}

		blueGameView = blueGame.getView();

		assertThat(blueGameView.getSystem("Fieras").getLocation()).isEqualTo(new Location(220, 100));
		assertThat(blueGameView.getSystem("Ajax").getLocation()).isEqualTo(new Location(180, 220));
	}

	@ParameterizedTest
	@EnumSource(Outcome.class)
	@SuppressWarnings("PMD.MissingSwitchDefault")
	void testSpaceCombat(Outcome outcome) {
		Game game = GameFactory.get()
			.create(GameOptions.forTestGameScenario() //
				.spaceCombatWinner(outcome)
				.fleetSpeedFactor(2000.0));
		game.registerAi(Player.WHITE);
		game.registerAi(Player.YELLOW);

		PlayerGame whiteGame = game.forPlayer(Player.WHITE);
		GameView whiteGameView = whiteGame.getView();
		FleetView fleetAtFieras = whiteGameView.getOrbiting(whiteGameView.getSystem("Fieras").getId()).orElseThrow();
		Map<ShipTypeView, Integer> previousFierasFleetShips = fleetAtFieras.getShips();

		PlayerGame blueGame = game.forPlayer(Player.BLUE);
		GameView blueGameView = blueGame.getView();

		FleetView fleetAtSol = blueGameView.getOrbiting(blueGameView.getSystem("Sol").getId()).orElseThrow();
		Map<ShipTypeView, Integer> previousSolFleetShips = fleetAtSol.getShips();
		blueGame.deployFleet(fleetAtSol.getId(), blueGameView.getSystem(new SystemId("s220x100")).getId(),
				fleetAtSol.getShips()
					.entrySet()
					.stream()
					.collect(Collectors.toMap(e -> e.getKey().getId(), Entry::getValue)));
		blueGameView = blueGame.getView();
		FleetId deployedFleedId = blueGameView.getFleets().iterator().next().getId();

		blueGame.finishTurn();

		blueGameView = blueGame.getView();
		whiteGameView = game.forPlayer(Player.WHITE).getView();

		BiFunction<Map<ShipTypeView, Integer>, GameView, Boolean> checkShipsHalfed = (previousShips, playerView) -> {
			Map<ShipTypeView, Integer> ships = playerView.getFleets(playerView.getPlayer())
				.stream()
				.findFirst()
				.get()
				.getShips();
			Map<ShipTypeView, Integer> doubledShips = ships.entrySet()
				.stream()
				.collect(Collectors.toMap(Entry::getKey, e -> e.getValue() * 2));

			return previousShips.equals(doubledShips);
		};

		switch (outcome) {
			case ATTACKER_WON -> {
				assertThat(blueGameView.getFleets(Player.BLUE)).hasSize(1);
				assertThat(checkShipsHalfed.apply(previousSolFleetShips, blueGameView)).isTrue();
				assertThat(blueGameView.getFleets(Player.WHITE)).hasSize(0);

				assertThat(whiteGameView.getFleets(Player.WHITE)).hasSize(0);
				assertThat(whiteGameView.getFleets(Player.BLUE)).hasSize(1);
			}
			case ATTACKER_RETREATED -> {
				assertThat(blueGameView.getFleets(Player.BLUE)).hasSize(1);
				assertThat(checkShipsHalfed.apply(previousSolFleetShips, blueGameView)).isTrue();
				assertThat(blueGameView.getFleets(Player.WHITE)).hasSize(1);

				assertThat(whiteGameView.getFleets(Player.WHITE)).hasSize(1);
				assertThat(checkShipsHalfed.apply(previousFierasFleetShips, whiteGameView)).isTrue();
				assertThat(whiteGameView.getFleets(Player.BLUE)).hasSize(1);
			}
			case DEFENDER_WON -> {
				assertThat(blueGameView.getFleets(Player.BLUE)).hasSize(0);
				assertThat(blueGameView.getFleets(Player.WHITE)).hasSize(0);

				assertThat(whiteGameView.getFleets(Player.WHITE)).hasSize(1);
				assertThat(checkShipsHalfed.apply(previousFierasFleetShips, whiteGameView)).isTrue();
				assertThat(whiteGameView.getFleets(Player.BLUE)).hasSize(0);
			}
		}

		FleetView blueFleet = blueGameView.getFleets(Player.BLUE).stream().findFirst().orElse(null);
		if (blueFleet != null) {
			assertThat(deployedFleedId)
				.isEqualTo(blueGameView.getSpaceCombats().iterator().next().getAttackerFleet().getId());
			assertThat(blueFleet.getFleetIdsBeforeArrive()).extracting(FleetBeforeArrival::getId)
				.contains(deployedFleedId);
			assertThat(blueFleet.getId()).isNotEqualTo(deployedFleedId);
		}
	}

	@Test
	void testSwitchToNextShipType() {
		Game game = GameFactory.get().create(GameOptions.forTestGameScenario());
		PlayerGame blueGame = game.forPlayer(Player.BLUE);

		List<String> shipNames = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			GameView blueGameState = blueGame.getView();

			shipNames.add(blueGameState.getSystem("Sol").getColonyView().get().getSpaceDock().get().getName());
			blueGame.nextShipType(blueGameState.getSystem("Sol").getColonyView().get().getId());
		}

		assertThat(shipNames).containsExactly("Scout", "Colony Ship", "Fighter");
	}

	@Test
	void testColonization() {
		Game game = GameFactory.get()
			.create(GameOptions.forTestGameScenario()
				// make the whole map reachable in a single turn for a simpler test setup
				.fleetRangeFactor(2000.0)
				.fleetSpeedFactor(2000.0)
				// decrease the number of turns of siege required to annex a system to 1
				.annexationSiegeRounds(1));
		game.registerAi(Player.WHITE);
		game.registerAi(Player.YELLOW);

		PlayerGame blueGame = game.forPlayer(Player.BLUE);

		GameView blueGameView = blueGame.getView();
		FleetView fleetAtSol = blueGameView.getOrbiting(blueGameView.getSystem("Sol").getId()).orElseThrow();
		blueGame.deployFleet(fleetAtSol.getId(), blueGameView.getSystem(new SystemId("s180x220")).getId(),
				Map.of(fleetAtSol.getShipType("Colony Ship").getId(), 1));
		blueGame.finishTurn();

		blueGameView = blueGame.getView();
		SystemView systemToColonize = blueGameView.getSystem(new SystemId("s180x220"));
		FleetView colonyFleet = blueGameView.getOrbiting(systemToColonize.getId()).get();
		blueGame.colonizeSystem(systemToColonize.getId(), colonyFleet.getId(), false);
		assertThat(blueGameView.getSystem(new SystemId("s180x220")).getColonyView()).isEmpty();
		blueGame.finishTurn();

		blueGameView = blueGame.getView();
		assertThat(blueGameView.getSystem(new SystemId("s180x220")).getColonyView().get().getPlayer())
			.isEqualTo(Player.BLUE);
	}

	@Test
	void testAnnexation() {
		Game game = GameFactory.get()
			.create(GameOptions.forTestGameScenario()
				// make the whole map reachable in a single turn for a simpler test setup
				.fleetRangeFactor(2000.0)
				.fleetSpeedFactor(2000.0)
				// decrease the number of turns of siege required to annex a system to 1
				.annexationSiegeRounds(1));
		game.registerAi(Player.WHITE);
		game.registerAi(Player.YELLOW);

		PlayerGame blueGame = game.forPlayer(Player.BLUE);

		GameView blueGameView = blueGame.getView();
		FleetView fleetAtSol = blueGameView.getOrbiting(blueGameView.getSystem("Sol").getId()).orElseThrow();
		blueGame.deployFleet(fleetAtSol.getId(), blueGameView.getSystem(new SystemId("s240x440")).getId(),
				Map.of(fleetAtSol.getShipType("Cruiser").getId(), 1));
		blueGame.finishTurn();

		blueGameView = blueGame.getView();
		assertThat(blueGameView.getSystem(new SystemId("s240x440")).getColonyView().get().getPlayer())
			.isEqualTo(Player.YELLOW);
		blueGame.finishTurn();

		blueGameView = blueGame.getView();
		SystemView systemToAnnex = blueGameView.getSystem(new SystemId("s240x440"));
		FleetView annexFleet = blueGameView.getOrbiting(systemToAnnex.getId()).get();
		blueGame.annexSystem(systemToAnnex.getColonyView().get().getId(), annexFleet.getId(), false);
		blueGame.finishTurn();

		blueGameView = blueGame.getView();
		assertThat(blueGameView.getSystem(new SystemId("s240x440")).getColonyView().get().getPlayer())
			.isEqualTo(Player.BLUE);
	}

}
