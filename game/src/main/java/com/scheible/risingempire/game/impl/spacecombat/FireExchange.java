package com.scheible.risingempire.game.impl.spacecombat;

/**
 * @author sj
 */
public class FireExchange {

	private final int round;

	private final int lostHitPoints;

	private final int damage;

	private final int shipCount;

	public FireExchange(final int round, final int lostHitPoints, final int damage, final int shipCount) {
		this.round = round;
		this.lostHitPoints = lostHitPoints;
		this.damage = damage;
		this.shipCount = shipCount;
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

	public int getShipCount() {
		return shipCount;
	}

}
