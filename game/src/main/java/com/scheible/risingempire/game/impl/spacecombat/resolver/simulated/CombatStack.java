package com.scheible.risingempire.game.impl.spacecombat.resolver.simulated;

import java.util.HashMap;
import java.util.Map;

import com.scheible.risingempire.game.impl.ship.AbstractWeapon;
import com.scheible.risingempire.game.impl.ship.BeamWeapon;
import com.scheible.risingempire.game.impl.ship.Missile;
import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.util.SeededRandom;

/**
 * @author sj
 */
class CombatStack {

	enum Side {

		ATTACKER, DEFENDER

	}

	final ShipDesign design;

	int count;

	final Side side;

	final Map<Missile, Integer> missileLaunches = new HashMap<>();

	private final int hitPoints;

	private int topShipHitPoints;

	private final SeededRandom random;

	CombatStack(ShipDesign design, int count, Side side, SeededRandom random) {
		this.design = design;
		this.hitPoints = design.getHitPoints();
		this.topShipHitPoints = design.getHitPoints();
		this.count = count;
		this.side = side;
		this.random = random;
	}

	int hitWith(AbstractWeapon weapon) {
		int attackValue = this.random.nextInt(weapon.getDamage().getMin(), weapon.getDamage().getMax() + 1);
		int effectiveAttackValue = attackValue - this.design.getHitsAbsorbedByShield()
				- (weapon instanceof BeamWeapon ? this.design.getBeamDefence() : this.design.getMissileDefence());

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
