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
				.spaceCombatOutcome(Outcome.ATTACKER_WON);
		}).turn((game, view) -> {
			FleetView fleetAtSol = view.orbiting(SOL_BLUE_HOME).orElseThrow();
			originalBlueShips.set(fleetAtSol.ships());
			game.deployFleet(fleetAtSol.id(), FIERAS_WHITE_HOME, fleetAtSol.ships());
		}).turn((game, view) -> {
			FleetView blueFleetAtFieras = view.orbiting(FIERAS_WHITE_HOME).orElseThrow();
			assertHalfShipCounts(originalBlueShips.get(), blueFleetAtFieras.ships());

			assertThat(testScenario.getWhiteView().orbiting(FIERAS_WHITE_HOME)).isEmpty();
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
				.spaceCombatOutcome(Outcome.DEFENDER_WON);
		}).turn((game, view) -> {
			FleetView fleetAtSol = view.orbiting(SOL_BLUE_HOME).orElseThrow();
			game.deployFleet(fleetAtSol.id(), FIERAS_WHITE_HOME, fleetAtSol.ships());

			originalWhiteShips.set(testScenario.getWhiteView().orbiting(FIERAS_WHITE_HOME).orElseThrow().ships());
		}).turn((game, view) -> {
			assertThat(view.fleets().values().stream().filter(f -> f.player() == Player.BLUE)).isEmpty();

			FleetView whiteFleetAtFieras = testScenario.getWhiteView().orbiting(FIERAS_WHITE_HOME).orElseThrow();
			assertHalfShipCounts(originalWhiteShips.get(), whiteFleetAtFieras.ships());
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
				.spaceCombatOutcome(Outcome.ATTACKER_RETREATED);
		}).turn((game, view) -> {
			FleetView fleetAtSol = view.orbiting(SOL_BLUE_HOME).orElseThrow();
			originalBlueShips.set(fleetAtSol.ships());
			game.deployFleet(fleetAtSol.id(), FIERAS_WHITE_HOME, fleetAtSol.ships());

			originalWhiteShips.set(testScenario.getWhiteView().orbiting(FIERAS_WHITE_HOME).orElseThrow().ships());
		}).turn((game, view) -> {
			assertThat(view.fleets()).hasSize(2);
			assertThat(view.fleets().values().stream().filter(f -> f.player() == Player.BLUE).toList().getFirst())
				.satisfies(f -> {
					assertThat(f.source()).contains(FIERAS_WHITE_HOME);
					assertThat(f.destination()).contains(SOL_BLUE_HOME);
					assertHalfShipCounts(originalBlueShips.get(), f.ships());
				});
			assertThat(view.fleets().values().stream().filter(f -> f.player() == Player.WHITE).toList().getFirst())
				.satisfies(f -> assertThat(f.orbiting()).contains(FIERAS_WHITE_HOME));

			FleetView whiteFleetAtFieras = testScenario.getWhiteView().orbiting(FIERAS_WHITE_HOME).orElseThrow();
			assertHalfShipCounts(originalWhiteShips.get(), whiteFleetAtFieras.ships());
		});
	}

	private static void assertHalfShipCounts(ShipsView previousShips, ShipsView currentShips) {
		assertThat(previousShips.types()).isEqualTo(currentShips.types());
		for (ShipTypeView type : previousShips.types()) {
			assertThat(currentShips.countByType(type) * 2).isEqualTo(previousShips.countByType(type));
		}
	}

}
