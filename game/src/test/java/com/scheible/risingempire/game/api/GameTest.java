package com.scheible.risingempire.game.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;

/**
 *
 * @author sj
 */
class GameTest {

	@Test
	void testExploration() {
		final Game game = GameFactory.get().create(GameOptions.forTestGameScenario());
		game.registerAi(Player.WHITE);
		game.registerAi(Player.YELLOW);

		final PlayerGame blueGame = game.forPlayer(Player.BLUE);
		GameView blueGameView = null;

		for (int i = 0; i < 5; i++) {
			blueGameView = blueGame.getView();

			final Set<SystemId> spaceCombatSystemIds = blueGameView.getSpaceCombats().stream()
					.map(SpaceCombatView::getSystemId).collect(Collectors.toSet());

			if (blueGameView.getRound() == 4) {
				assertThat(spaceCombatSystemIds).containsExactly(new SystemId("s220x100"));
				assertThat(blueGameView.getJustExploredSystemIds()).isEmpty();
			} else if (blueGameView.getRound() == 5) {
				assertThat(blueGameView.getJustExploredSystemIds()).containsExactly(new SystemId("s180x220"));
				assertThat(spaceCombatSystemIds).isEmpty();
			} else {
				assertThat(blueGameView.getJustExploredSystemIds()).isEmpty();
				assertThat(spaceCombatSystemIds).isEmpty();
			}

			if (blueGameView.getRound() == 1) {
				final FleetView fleetAtSol = blueGameView.getOrbiting(blueGameView.getSystem("Sol").getId())
						.orElseThrow();
				blueGame.deployFleet(fleetAtSol.getId(), blueGameView.getSystem(new SystemId("s220x100")).getId(),
						Map.of(fleetAtSol.getShipType("Scout").getId(), 1));
				blueGame.deployFleet(fleetAtSol.getId(), blueGameView.getSystem(new SystemId("s180x220")).getId(),
						Map.of(fleetAtSol.getShipType("Scout").getId(), 1));
			} else if (blueGameView.getRound() == 4) {
				final GameView gameState2 = blueGameView;
				blueGameView.getOrbiting(blueGameView.getSystem("Fieras").getId())
						.ifPresent(fleetAtFieras -> blueGame.deployFleet(fleetAtFieras.getId(),
								gameState2.getSystem("Sol").getId(),
								Map.of(fleetAtFieras.getShipType("Scout").getId(), 1)));
			}

			for (final TechGroupView techGroup : blueGameView.getSelectTechs()) {
				blueGame.selectTech(techGroup.iterator().next().getId());
			}

			blueGame.finishTurn();
		}

		blueGameView = blueGame.getView();

		assertThat(blueGameView.getSystem("Fieras").getLocation()).isEqualTo(new Location(220, 100));
		assertThat(blueGameView.getSystem("Ajax").getLocation()).isEqualTo(new Location(180, 220));
	}

	@Test
	void testRetreatingFleet() {
		final Game game = GameFactory.get().create(GameOptions.forTestGameScenario() //
				.spaceCombatWinner(Outcome.ATTACKER_RETREATED).fleetSpeedFactor(2000.0));
		game.registerAi(Player.WHITE);
		game.registerAi(Player.YELLOW);

		final PlayerGame blueGame = game.forPlayer(Player.BLUE);
		GameView blueGameView = blueGame.getView();

		final FleetView fleetAtSol = blueGameView.getOrbiting(blueGameView.getSystem("Sol").getId()).orElseThrow();
		blueGame.deployFleet(fleetAtSol.getId(), blueGameView.getSystem(new SystemId("s220x100")).getId(), fleetAtSol
				.getShips().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), Entry::getValue)));
		blueGameView = blueGame.getView();
		final FleetId deployedFleedId = blueGameView.getFleets().iterator().next().getId();

		blueGame.finishTurn();

		blueGameView = blueGame.getView();
		for (final FleetView fleet : blueGameView.getFleets().stream().filter(f -> f.getPlayer() == Player.BLUE)
				.collect(Collectors.toSet())) {
			assertThat(deployedFleedId)
					.isEqualTo(blueGameView.getSpaceCombats().iterator().next().getAttackerFleet().getId());
			assertThat(fleet.getFleetIdsBeforeArrive()).extracting(FleetBeforeArrival::getId).contains(deployedFleedId);
			assertThat(fleet.getId()).isNotEqualTo(deployedFleedId);
		}
	}

	@Test
	void testSwitchToNextShipType() {
		final Game game = GameFactory.get().create(GameOptions.forTestGameScenario());
		final PlayerGame blueGame = game.forPlayer(Player.BLUE);

		final List<String> shipNames = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			final GameView blueGameState = blueGame.getView();

			shipNames.add(blueGameState.getSystem("Sol").getColonyView().get().getSpaceDock().get().getName());
			blueGame.nextShipType(blueGameState.getSystem("Sol").getColonyView().get().getId());
		}

		assertThat(shipNames).containsExactly("Scout", "Colony Ship", "Fighter");
	}
}
