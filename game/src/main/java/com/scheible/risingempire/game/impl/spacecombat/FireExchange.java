package com.scheible.risingempire.game.impl.spacecombat;

/**
 * @author sj
 */
public class FireExchange {

	private final int round;

	private final int lostHitPoints;

	private final int damage;

	private final int shipCount;

	public FireExchange(int round, int lostHitPoints, int damage, int shipCount) {
		this.round = round;
		this.lostHitPoints = lostHitPoints;
		this.damage = damage;
		this.shipCount = shipCount;
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

	public int getShipCount() {
		return this.shipCount;
	}

}
