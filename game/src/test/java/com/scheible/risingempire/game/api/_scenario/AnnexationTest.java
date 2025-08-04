package com.scheible.risingempire.game.api._scenario;

import com.scheible.risingempire.game.api._testgame.AbstractGameTest;
import com.scheible.risingempire.game.api._testgame.TestScenario;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
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
				.annexationSiegeRounds(1)
				.predefinedSpaceCombatOutcome(Outcome.ATTACKER_WON);
		}).turn((game, view) -> {
			FleetView fleetAtSol = view.orbiting(SOL_BLUE_HOME).orElseThrow();
			game.deployFleet(fleetAtSol.id(), SPICIA_WHITE, fleetAtSol.ships());
		}).turn((game, view) -> {
			assertThat(view.annexableSystemIds()).doesNotContain(SPICIA_WHITE);
			assertThat(view.system(SPICIA_WHITE).colony().map(ColonyView::player)).contains(Player.WHITE);
		}).turn((game, view) -> {
			assertThat(view.annexableSystemIds()).contains(SPICIA_WHITE);
			game.annexSystem(SPICIA_WHITE.toColonyId(), view.orbiting(SPICIA_WHITE).orElseThrow().id(), false);
			assertThat(view.system(SPICIA_WHITE).colony().map(ColonyView::player)).contains(Player.WHITE);
		}).turn((game, view) -> {
			assertThat(view.system(SPICIA_WHITE).colony().map(ColonyView::player)).contains(Player.BLUE);
		});
	}

}
