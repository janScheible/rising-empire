package com.scheible.risingempire.game.impl.spacecombat.resolver.simulated;

import static com.scheible.risingempire.game.api.view.ship.ShipSize.MEDIUM;
import static com.scheible.risingempire.game.api.view.ship.ShipSize.SMALL;
import static com.scheible.risingempire.game.impl.ship.ShipDesignTest.LASER;
import static com.scheible.risingempire.game.impl.ship.ShipDesignTest.NUCLEAR_MISSILE;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.game.impl.spacecombat.resolver.simulated.SimulatingSpaceCombatResolver.SpaceCombatSummary;

/**
 *
 * @author sj
 */
public class SimulatingSpaceCombatResolverTest {

	@Test
	public void testSomeMethod() {
		final ShipDesign attackingFighterDesing = ShipDesign.builder().name("Destroyer").size(MEDIUM).look(0)
				.computer(0).shield(0).ecm(0).armor("Titanium", 1.0).engine("Retro", 1).maneuver(1)
				.weapons(1, LASER, 1, NUCLEAR_MISSILE).specials();
		System.out.println("attackingFighterDesing: " + attackingFighterDesing);

		final ShipDesign defendingFighterDesing = ShipDesign.builder().name("Fighter").size(SMALL).look(0).computer(0)
				.shield(0).ecm(0).armor("Titanium", 1.0).engine("Retro", 1).maneuver(1).weapons(1, LASER).specials();
		System.out.println("defendingFighterDesing: " + defendingFighterDesing);

		SpaceCombatSummary summary;
		System.out.println((summary = new SimulatingSpaceCombatResolver().simulate(Map.of(attackingFighterDesing, 3),
				Map.of(defendingFighterDesing, 7))).outcome);

		System.out.println(summary);
	}
}
