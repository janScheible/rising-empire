package com.scheible.risingempire.game.impl.ship;

/**
 * @author sj
 */
public abstract class AbstractSpecial {

	protected final String name;

	protected AbstractSpecial(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
