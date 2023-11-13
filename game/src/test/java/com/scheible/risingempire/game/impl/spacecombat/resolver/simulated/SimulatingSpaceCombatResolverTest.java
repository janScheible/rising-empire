package com.scheible.risingempire.game.impl.spacecombat.resolver.simulated;

import java.util.Map;

import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.game.impl.spacecombat.resolver.simulated.SimulatingSpaceCombatResolver.SpaceCombatSummary;
import org.junit.jupiter.api.Test;

import static com.scheible.risingempire.game.api.view.ship.ShipSize.MEDIUM;
import static com.scheible.risingempire.game.api.view.ship.ShipSize.SMALL;
import static com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome.DEFENDER_WON;
import static com.scheible.risingempire.game.impl.ship.ShipDesignTest.LASER;
import static com.scheible.risingempire.game.impl.ship.ShipDesignTest.NUCLEAR_MISSILE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class SimulatingSpaceCombatResolverTest {

	@Test
	public void testSimulate() {
		final ShipDesign attackingFighterDesing = ShipDesign.builder()
			.name("Destroyer")
			.size(MEDIUM)
			.look(0)
			.computer(0)
			.shield(0)
			.ecm(0)
			.armor("Titanium", 1.0)
			.engine("Retro", 1)
			.maneuver(1)
			.weapons(1, LASER, 1, NUCLEAR_MISSILE)
			.specials();

		final ShipDesign defendingFighterDesing = ShipDesign.builder()
			.name("Fighter")
			.size(SMALL)
			.look(0)
			.computer(0)
			.shield(0)
			.ecm(0)
			.armor("Titanium", 1.0)
			.engine("Retro", 1)
			.maneuver(1)
			.weapons(1, LASER)
			.specials();

		SpaceCombatSummary summary = new SimulatingSpaceCombatResolver().simulate(Map.of(attackingFighterDesing, 3),
				Map.of(defendingFighterDesing, 7));

		assertThat(summary.outcome).isEqualTo(DEFENDER_WON);
	}

}
