package com.scheible.risingempire.game.impl2.apiinternal;

/**
 * Speed in parsecs per round.
 */
public record Speed(Parsec distance) implements Comparable<Speed> {

	public Speed(String distance) {
		this(new Parsec(distance));
	}

	public Speed(double distance) {
		this(new Parsec(distance));
	}

	public Speed multiply(double factor) {
		return new Speed(this.distance.multiply(new Parsec(factor)));
	}

	public Speed add(Speed augend) {
		return new Speed(this.distance.add(augend.distance));
	}

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
