package com.scheible.risingempire.game.api.view.spacecombat;

import java.util.Objects;

/**
 *
 * @author sj
 */
public class FireExchangeView {

	private final int round;
	private final int lostHitPoints;
	private final int damage;
	private final int count;

	public FireExchangeView(final int round, final int lostHitPoints, final int damage, final int count) {
		this.round = round;
		this.lostHitPoints = lostHitPoints;
		this.damage = damage;
		this.count = count;
	}

	public int getRound() {
		return round;
	}

	public int getLostHitPoints() {
		return lostHitPoints;
	}

	public int getDamage() {
		return damage;
	}

	public int getCount() {
		return count;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && obj.getClass().equals(getClass())) {
			final FireExchangeView other = (FireExchangeView) obj;
			return Objects.equals(round, other.round) && Objects.equals(lostHitPoints, other.lostHitPoints)
					&& Objects.equals(damage, other.damage) && Objects.equals(count, other.count);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(round, lostHitPoints, damage, count);
	}
}
