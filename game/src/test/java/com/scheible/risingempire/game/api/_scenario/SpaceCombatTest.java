package com.scheible.risingempire.game.api._scenario;

import java.util.concurrent.atomic.AtomicReference;

import com.scheible.risingempire.game.api._testgame.AbstractGameTest;
import com.scheible.risingempire.game.api._testgame.TestScenario;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class SpaceCombatTest extends AbstractGameTest {

	@Test
	public void testSpaceCombatAttackerWon(TestScenario testScenario) {
		AtomicReference<ShipsView> originalBlueShips = new AtomicReference<>();

		testScenario.customize(options -> {
			options
				// make the whole map reachable in a single turn for a simpler test setup
				.fleetRangeFactor(30.0)
				.fleetSpeedFactor(30.0)
				.spaceCombatWinner(Outcome.ATTACKER_WON);
		}).turn((game, view) -> {
			FleetView fleetAtSol = view.getOrbiting(SOL_BLUE_HOME).orElseThrow();
			originalBlueShips.set(fleetAtSol.getShips());
			game.deployFleet(fleetAtSol.getId(), FIERAS_WHITE_HOME, fleetAtSol.getShips());
		}).turn((game, view) -> {
			FleetView blueFleetAtFieras = view.getOrbiting(FIERAS_WHITE_HOME).orElseThrow();
			assertHalfShipCounts(originalBlueShips.get(), blueFleetAtFieras.getShips());

			assertThat(testScenario.getWhiteView().getOrbiting(FIERAS_WHITE_HOME)).isEmpty();
		});
	}

	@Test
	public void testSpaceCombatDefenderWon(TestScenario testScenario) {
		AtomicReference<ShipsView> originalWhiteShips = new AtomicReference<>();

		testScenario.customize(options -> {
			options
				// make the whole map reachable in a single turn for a simpler test setup
				.fleetRangeFactor(30.0)
				.fleetSpeedFactor(30.0)
				.spaceCombatWinner(Outcome.DEFENDER_WON);
		}).turn((game, view) -> {
			FleetView fleetAtSol = view.getOrbiting(SOL_BLUE_HOME).orElseThrow();
			game.deployFleet(fleetAtSol.getId(), FIERAS_WHITE_HOME, fleetAtSol.getShips());

			originalWhiteShips.set(testScenario.getWhiteView().getOrbiting(FIERAS_WHITE_HOME).orElseThrow().getShips());
		}).turn((game, view) -> {
			assertThat(view.getFleets().stream().filter(f -> f.getPlayer() == Player.BLUE)).isEmpty();

			FleetView whiteFleetAtFieras = testScenario.getWhiteView().getOrbiting(FIERAS_WHITE_HOME).orElseThrow();
			assertHalfShipCounts(originalWhiteShips.get(), whiteFleetAtFieras.getShips());
		});
	}

	@Test
	public void testSpaceCombatAttackerRetreated(TestScenario testScenario) {
		AtomicReference<ShipsView> originalBlueShips = new AtomicReference<>();
		AtomicReference<ShipsView> originalWhiteShips = new AtomicReference<>();

		testScenario.customize(options -> {
			options
				// make the whole map reachable in a single turn for a simpler test setup
				.fleetRangeFactor(30.0)
				.fleetSpeedFactor(30.0)
				.spaceCombatWinner(Outcome.ATTACKER_RETREATED);
		}).turn((game, view) -> {
			FleetView fleetAtSol = view.getOrbiting(SOL_BLUE_HOME).orElseThrow();
			originalBlueShips.set(fleetAtSol.getShips());
			game.deployFleet(fleetAtSol.getId(), FIERAS_WHITE_HOME, fleetAtSol.getShips());

			originalWhiteShips.set(testScenario.getWhiteView().getOrbiting(FIERAS_WHITE_HOME).orElseThrow().getShips());
		}).turn((game, view) -> {
			assertThat(view.getFleets()).hasSize(2);
			assertThat(view.getFleets().stream().filter(f -> f.getPlayer() == Player.BLUE).toList().getFirst())
				.satisfies(f -> {
					assertThat(f.getSource()).contains(FIERAS_WHITE_HOME);
					assertThat(f.getDestination()).contains(SOL_BLUE_HOME);
					assertHalfShipCounts(originalBlueShips.get(), f.getShips());
				});
			assertThat(view.getFleets().stream().filter(f -> f.getPlayer() == Player.WHITE).toList().getFirst())
				.satisfies(f -> assertThat(f.getOrbiting()).contains(FIERAS_WHITE_HOME));

			FleetView whiteFleetAtFieras = testScenario.getWhiteView().getOrbiting(FIERAS_WHITE_HOME).orElseThrow();
			assertHalfShipCounts(originalWhiteShips.get(), whiteFleetAtFieras.getShips());
		});
	}

	private static void assertHalfShipCounts(ShipsView previousShips, ShipsView currentShips) {
		assertThat(previousShips.getTypes()).isEqualTo(currentShips.getTypes());
		for (ShipTypeView type : previousShips.getTypes()) {
			assertThat(currentShips.getCountByType(type) * 2).isEqualTo(previousShips.getCountByType(type));
		}
	}

}
