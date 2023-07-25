package com.scheible.risingempire.game.impl.ship;

import static com.scheible.risingempire.game.api.view.ship.ShipSize.SMALL;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.scheible.risingempire.game.impl.ship.AbstractWeapon.Damage;
import com.scheible.risingempire.game.impl.ship.Missile.RackSize;

/**
 *
 * @author sj
 */
public class ShipDesignTest {

	public static final AbstractWeapon LASER = new BeamWeapon("Laser", new Damage(1, 4));
	public static final AbstractWeapon NUCLEAR_MISSILE = new Missile("Nuclear Missile", new Damage(4), RackSize.TWO);

	@Test
	void testFighter() {
		final ShipDesign fighterDesing = ShipDesign.builder().name("Fighter").size(SMALL).look(0).computer(0).shield(0)
				.ecm(0).armor("Titanium", 1.0).engine("Retro", 1).maneuver(1).weapons(1, LASER, 1, NUCLEAR_MISSILE)
				.specials();

		assertThat(fighterDesing.getName()).isEqualTo("Fighter");
		assertThat(fighterDesing.getSize()).isEqualTo(SMALL);
	}
}
