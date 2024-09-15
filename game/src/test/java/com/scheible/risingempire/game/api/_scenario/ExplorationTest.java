package com.scheible.risingempire.game.api._scenario;

import com.scheible.risingempire.game.api._testgame.AbstractGameTest;
import com.scheible.risingempire.game.api._testgame.TestScenario;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import org.junit.jupiter.api.Test;

import static com.scheible.risingempire.game.api._testgame.AbstractGameTest.AJAX;
import static com.scheible.risingempire.game.api._testgame.AbstractGameTest.SOL_BLUE_HOME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class ExplorationTest extends AbstractGameTest {

	@Test
	public void testExplorationUncolonizedSystem(TestScenario testScenario) {
		testScenario.customize(options -> {
			options
				// make the whole map reachable in a single turn for a simpler test setup
				.fleetRangeFactor(30.0)
				.fleetSpeedFactor(30.0);
		}).turn((game, view) -> {
			FleetView fleetAtSol = view.orbiting(SOL_BLUE_HOME).orElseThrow();
			game.deployFleet(fleetAtSol.id(), AJAX, fleetAtSol.ships());
			assertThat(view.system(AJAX).starName()).isEmpty();
		}).turn((game, view) -> {
			assertThat(view.system(AJAX).starName()).contains("Ajax");
			assertThat(view.system(AJAX).colony()).isEmpty();
		});
	}

	@Test
	public void testExplorationColonizedSystem(TestScenario testScenario) {
		testScenario.customize(options -> {
			options
				// make the whole map reachable in a single turn for a simpler test setup
				.fleetRangeFactor(30.0)
				.fleetSpeedFactor(30.0);
		}).turn((game, view) -> {
			FleetView fleetAtSol = view.orbiting(SOL_BLUE_HOME).orElseThrow();
			game.deployFleet(fleetAtSol.id(), SPICIA_WHITE, fleetAtSol.ships());
		}).turn((game, view) -> {
			assertThat(view.system(SPICIA_WHITE).starName()).contains("Spicia");
			assertThat(view.system(SPICIA_WHITE).colony().map(ColonyView::player)).contains(Player.WHITE);
		});
	}

}
