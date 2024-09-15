package com.scheible.risingempire.game.api._scenario;

import com.scheible.risingempire.game.api._testgame.AbstractGameTest;
import com.scheible.risingempire.game.api._testgame.TestScenario;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class ColonizationTest extends AbstractGameTest {

	@Test
	public void testColonization(TestScenario testScenario) {
		testScenario.customize(options -> {
			options
				// make the whole map reachable in a single turn for a simpler test setup
				.fleetRangeFactor(30.0)
				.fleetSpeedFactor(30.0);
		}).turn((game, view) -> {
			FleetView fleetAtSol = view.orbiting(SOL_BLUE_HOME).orElseThrow();
			game.deployFleet(fleetAtSol.id(), AJAX, fleetAtSol.ships().partByName("Colony Ship", 1));
		}).turn((game, view) -> {
			FleetView colonyFleet = view.orbiting(AJAX).get();
			game.colonizeSystem(AJAX, colonyFleet.id(), false);

			assertThat(view.system(AJAX).colony()).isEmpty();
		}).turn((game, view) -> {
			assertThat(view.system(AJAX).colony().get().player()).isEqualTo(Player.BLUE);
		});
	}

}
