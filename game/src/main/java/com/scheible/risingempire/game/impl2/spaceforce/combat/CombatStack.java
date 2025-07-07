package com.scheible.risingempire.game.impl2.spaceforce.combat;

import java.util.HashMap;
import java.util.Map;

import com.scheible.risingempire.game.impl2.spaceforce.combat.weapon.CombatBeamWeapon;
import com.scheible.risingempire.game.impl2.spaceforce.combat.weapon.CombatMissile;
import com.scheible.risingempire.game.impl2.spaceforce.combat.weapon.CombatWeapon;
import com.scheible.risingempire.util.SeededRandom;

/**
 * @author sj
 */
class CombatStack {

	enum Side {

		ATTACKER, DEFENDER

	}

	final ShipCombatSpecs design;

	int count;

	final Side side;

	final Map<CombatMissile, Integer> missileLaunches = new HashMap<>();

	private final int hitPoints;

	private int topShipHitPoints;

	private final SeededRandom random;

	CombatStack(ShipCombatSpecs design, int count, Side side, SeededRandom random) {
		this.design = design;
		this.hitPoints = design.hitPoints();
		this.topShipHitPoints = design.hitPoints();
		this.count = count;
		this.side = side;
		this.random = random;
	}

	int hitWith(CombatWeapon weapon) {
		int attackValue = this.random.nextInt(weapon.damage().min(), weapon.damage().max() + 1);
		int effectiveAttackValue = attackValue - this.design.hitsAbsorbedByShield()
				- (weapon instanceof CombatBeamWeapon ? this.design.beamDefence() : this.design.missileDefence());

		if (effectiveAttackValue > 0) {
			this.topShipHitPoints = Math.max(0, this.topShipHitPoints - effectiveAttackValue);
			if (this.topShipHitPoints <= 0) {
				this.count = Math.max(0, this.count - 1);
				this.topShipHitPoints = this.hitPoints;
			}
		}

		return Math.max(0, effectiveAttackValue);
	}

	boolean isDestroyed() {
		return this.count == 0;
	}

	int getDamage() {
		return this.hitPoints - this.topShipHitPoints;
	}

}
