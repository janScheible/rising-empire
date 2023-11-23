package com.scheible.risingempire.game.api.view.spacecombat;

import java.util.Objects;

/**
 * @author sj
 */
public class FireExchangeView {

	private final int round;

	private final int lostHitPoints;

	private final int damage;

	private final int count;

	public FireExchangeView(int round, int lostHitPoints, int damage, int count) {
		this.round = round;
		this.lostHitPoints = lostHitPoints;
		this.damage = damage;
		this.count = count;
	}

	public int getRound() {
		return this.round;
	}

	public int getLostHitPoints() {
		return this.lostHitPoints;
	}

	public int getDamage() {
		return this.damage;
	}

	public int getCount() {
		return this.count;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj != null && obj.getClass().equals(getClass())) {
			FireExchangeView other = (FireExchangeView) obj;
			return Objects.equals(this.round, other.round) && Objects.equals(this.lostHitPoints, other.lostHitPoints)
					&& Objects.equals(this.damage, other.damage) && Objects.equals(this.count, other.count);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.round, this.lostHitPoints, this.damage, this.count);
	}

}
