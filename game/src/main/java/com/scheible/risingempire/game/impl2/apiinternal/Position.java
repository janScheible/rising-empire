package com.scheible.risingempire.game.impl2.apiinternal;

public record Position(Parsec x, Parsec y) {

	public Position(double x, double y) {
		this(new Parsec(x), new Parsec(y));
	}

	public Position(String x, String y) {
		this(new Parsec(x), new Parsec(y));
	}

	public static Position fromPlainString(String plain) {
		String[] parts = plain.split("x");
		if (parts.length == 2) {
			return new Position(Parsec.fromPlainString(parts[0]), Parsec.fromPlainString(parts[1]));
		}
		else {
			throw new IllegalArgumentException("'" + plain + "' is an invalid plain position string!");
		}
	}

	public Position subtract(Position other) {
		return new Position(this.x.subtract(other.x), this.y.subtract(other.y));
	}

	/**
	 * Distance in relation to reference origin.
	 */
	public Parsec length() {
		return this.x.multiply(this.x).add(this.y.multiply(this.y)).sqrt();
	}

	public static Position interpolate(Position from, Position to, Position current, Parsec delta) {
		Parsec totalLength = to.subtract(from).length();
		Parsec newLength = current.subtract(from).length().add(delta);

		if (newLength.greaterThan(totalLength)) {
			return to;
		}
		else {
			Parsec deltaX = to.x.subtract(from.x).multiply(newLength, totalLength);
			Parsec deltaY = to.y.subtract(from.y).multiply(newLength, totalLength);
			return new Position(from.x.add(deltaX), from.y.add(deltaY));
		}
	}

	public String toPlainString() {
		return this.x.toPlainString() + "x" + this.y.toPlainString();
	}
}
