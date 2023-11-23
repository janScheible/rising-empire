package com.scheible.risingempire.game.impl.spacecombat.resolver.simulated;

import java.util.Map;

import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl.ship.AbstractWeapon;
import com.scheible.risingempire.game.impl.ship.AbstractWeapon.Damage;
import com.scheible.risingempire.game.impl.ship.BeamWeapon;
import com.scheible.risingempire.game.impl.ship.Missile;
import com.scheible.risingempire.game.impl.ship.Missile.RackSize;
import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.game.impl.spacecombat.resolver.simulated.SimulatingSpaceCombatResolver.SpaceCombatSummary;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class SimulatingSpaceCombatResolverTest {

	private static final AbstractWeapon LASER = new BeamWeapon("Laser", new Damage(1, 4));

	private static final AbstractWeapon NUCLEAR_MISSILE = new Missile("Nuclear Missile", new Damage(4), RackSize.TWO);

	@Test
	void testSimulate() {
		ShipDesign attackingFighterDesing = ShipDesign.builder()
			.name("Destroyer")
			.size(ShipSize.MEDIUM)
			.look(0)
			.computer(0)
			.shield(0)
			.ecm(0)
			.armor("Titanium", 1.0)
			.engine("Retro", 1)
			.maneuver(1)
			.weapons(1, LASER, 1, NUCLEAR_MISSILE)
			.specials();

		ShipDesign defendingFighterDesing = ShipDesign.builder()
			.name("Fighter")
			.size(ShipSize.SMALL)
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

		assertThat(summary.outcome).isEqualTo(Outcome.DEFENDER_WON);
	}

}
