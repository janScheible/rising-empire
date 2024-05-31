package com.scheible.risingempire.game.api._scenario;

import com.scheible.risingempire.game.api._testgame.AbstractGameTest;
import com.scheible.risingempire.game.api._testgame.TestScenario;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import org.junit.jupiter.api.Test;

import static com.scheible.risingempire.game.api._testgame.AbstractGameTest.SOL_BLUE_HOME;
import static com.scheible.risingempire.game.api._testgame.AbstractGameTest.SPICIA_WHITE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class AnnexationTest extends AbstractGameTest {

	@Test
	public void testAnnexation(TestScenario testScenario) {
		testScenario.customize(options -> {
			options
				// make the whole map reachable in a single turn for a simpler test setup
				.fleetRangeFactor(30.0)
				.fleetSpeedFactor(30.0)
				// decrease the number of turns of siege required to annex a system to 1
				.annexationSiegeRounds(1);
		}).turn((game, view) -> {
			FleetView fleetAtSol = view.getOrbiting(SOL_BLUE_HOME).orElseThrow();
			game.deployFleet(fleetAtSol.getId(), SPICIA_WHITE, fleetAtSol.getShips());
		}).turn((game, view) -> {
			assertThat(view.getAnnexableSystemIds()).doesNotContain(SPICIA_WHITE);
			assertThat(view.getSystem(SPICIA_WHITE).getColonyView().map(ColonyView::getPlayer)).contains(Player.WHITE);
		}).turn((game, view) -> {
			assertThat(view.getAnnexableSystemIds()).contains(SPICIA_WHITE);
			game.annexSystem(SPICIA_WHITE.toColonyId(), view.getOrbiting(SPICIA_WHITE).orElseThrow().getId(), false);
			assertThat(view.getSystem(SPICIA_WHITE).getColonyView().map(ColonyView::getPlayer)).contains(Player.WHITE);
		}).turn((game, view) -> {
			assertThat(view.getSystem(SPICIA_WHITE).getColonyView().map(ColonyView::getPlayer)).contains(Player.BLUE);
		});
	}

}
