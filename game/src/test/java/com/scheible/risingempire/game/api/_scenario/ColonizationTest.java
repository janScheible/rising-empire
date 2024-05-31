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
			FleetView fleetAtSol = view.getOrbiting(SOL_BLUE_HOME).orElseThrow();
			game.deployFleet(fleetAtSol.getId(), AJAX, fleetAtSol.getShips().getPartByName("Colony Ship", 1));
		}).turn((game, view) -> {
			FleetView colonyFleet = view.getOrbiting(AJAX).get();
			game.colonizeSystem(AJAX, colonyFleet.getId(), false);

			assertThat(view.getSystem(AJAX).getColonyView()).isEmpty();
		}).turn((game, view) -> {
			assertThat(view.getSystem(AJAX).getColonyView().get().getPlayer()).isEqualTo(Player.BLUE);
		});
	}

}
