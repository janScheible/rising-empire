package com.scheible.risingempire.game.impl.spacecombat.resolver.simulated;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.scheible.risingempire.game.impl.ship.AbstractWeapon;
import com.scheible.risingempire.game.impl.ship.BeamWeapon;
import com.scheible.risingempire.game.impl.ship.Missile;
import com.scheible.risingempire.game.impl.ship.ShipDesign;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author sj
 */
class CombatStack {

	enum Side {

		ATTACKER, DEFENDER

	}

	final ShipDesign design;

	private final int hitPoints;

	private int topShipHitPoints;

	final int previousCount;

	int count;

	final Side side;

	final Map<Missile, Integer> missileLaunches = new HashMap<>();

	CombatStack(final ShipDesign design, final int count, final Side side) {
		this.design = design;
		this.hitPoints = design.getHitPoints();
		this.topShipHitPoints = design.getHitPoints();
		this.previousCount = count;
		this.count = count;
		this.side = side;
	}

	@SuppressFBWarnings(value = "PREDICTABLE_RANDOM", justification = "Should be random enough.")
	int hitWith(final AbstractWeapon weapon) {
		final int attackValue = ThreadLocalRandom.current()
			.nextInt(weapon.getDamage().getMin(), weapon.getDamage().getMax() + 1);
		final int effectiveAttackValue = attackValue - design.getHitsAbsorbedByShield()
				- (weapon instanceof BeamWeapon ? design.getBeamDefence() : design.getMissileDefence());

		if (effectiveAttackValue > 0) {
			topShipHitPoints = Math.max(0, topShipHitPoints - effectiveAttackValue);
			if (topShipHitPoints <= 0) {
				count = Math.max(0, count - 1);
				topShipHitPoints = hitPoints;
			}
		}

		return Math.max(0, effectiveAttackValue);
	}

	boolean isDestroyed() {
		return count == 0;
	}

	int getDamage() {
		return hitPoints - topShipHitPoints;
	}

}
