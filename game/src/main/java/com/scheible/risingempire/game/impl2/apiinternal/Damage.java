package com.scheible.risingempire.game.impl2.apiinternal;

/**
 * @author sj
 */
public record Damage(int min, int max) {

	public Damage(int damage) {
		this(damage, damage);
	}

}
