package com.scheible.risingempire.game.impl2.apiinternal;

/**
 * Speed in parsecs per round.
 */
public record Speed(Parsec distance) implements Comparable<Speed> {

	@Override
	public int compareTo(Speed o) {
		return this.distance.compareTo(o.distance);
	}

	public static Speed fromPlainString(String plain) {
		return new Speed(Parsec.fromPlainString(plain));
	}

	public String toPlainString() {
		return this.distance.toPlainString();
	}
}
