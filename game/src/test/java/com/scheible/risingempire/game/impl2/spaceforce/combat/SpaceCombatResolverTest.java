package com.scheible.risingempire.game.impl2.spaceforce.combat;

import java.util.Map;

import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl2.apiinternal.Damage;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.spaceforce.combat.weapon.CombatBeamWeapon;
import com.scheible.risingempire.game.impl2.spaceforce.combat.weapon.CombatMissile;
import com.scheible.risingempire.game.impl2.spaceforce.combat.weapon.CombatWeapon;
import com.scheible.risingempire.util.SeededRandom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class SpaceCombatResolverTest {

	private static final CombatWeapon LASER = new CombatBeamWeapon("Laser", new Damage(1, 4));

	private static final CombatWeapon NUCLEAR_MISSILE = new CombatMissile("Nuclear Missile", new Damage(4), 2);

	@Test
	void testResolve() {
		ShipCombatSpecs attackingFighterDesing = ShipCombatSpecs.builder()
			.shipClassId(new ShipClassId("fighter"))
			.hitPoints(18)
			.hitsAbsorbedByShield(0)
			.beamDefence(2)
			.missileDefence(2)
			.initiative(2)
			.combatSpeed(1)
			.weapons(Map.of(LASER, 1, NUCLEAR_MISSILE, 1))
			.battleScanner(true)
			.build();

		ShipCombatSpecs defendingFighterDesing = ShipCombatSpecs.builder()
			.shipClassId(new ShipClassId("fighter"))
			.hitPoints(3)
			.hitsAbsorbedByShield(0)
			.beamDefence(3)
			.missileDefence(3)
			.initiative(3)
			.combatSpeed(1)
			.weapons(Map.of(LASER, 1))
			.battleScanner(false)
			.build();

		ResolvedSpaceCombat spaceCombat = new SpaceCombatResolverImpl(null, new SeededRandom(345_492_973_976L))
			.resolve(Map.of(attackingFighterDesing, 3), Map.of(defendingFighterDesing, 7));

		assertThat(spaceCombat.outcome()).isEqualTo(Outcome.DEFENDER_WON);
	}

}
