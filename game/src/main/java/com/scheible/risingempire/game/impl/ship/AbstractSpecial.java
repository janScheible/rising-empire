package com.scheible.risingempire.game.impl.ship;

/**
 *
 * @author sj
 */
public abstract class AbstractSpecial {

	protected final String name;

	protected AbstractSpecial(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
