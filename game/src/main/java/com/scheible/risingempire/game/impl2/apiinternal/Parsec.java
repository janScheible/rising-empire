package com.scheible.risingempire.game.impl2.apiinternal;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * A quantity with unit Parsec with milliparsec precision.
 */
public record Parsec(BigDecimal quantity) implements Comparable<Parsec> {

	private static final BigDecimal THOUSAND = new BigDecimal(1000);

	public Parsec {
		if (quantity.scale() != 3) {
			quantity = BigDecimal.valueOf(quantity.multiply(THOUSAND).intValue(), 3);
		}
	}

	public Parsec(double quantity) {
		this(BigDecimal.valueOf(quantity).setScale(3));
	}

	public Parsec(String quantity) {
		this(new BigDecimal(quantity));
	}

	public static Parsec fromMilliparsec(long quantity) {
		return new Parsec(BigDecimal.valueOf(quantity, 3));
	}

	public static Parsec fromPlainString(String plain) {
		return fromMilliparsec(Long.parseLong(plain));
	}

	public Parsec add(Parsec other) {
		return new Parsec(this.quantity.add(other.quantity));
	}

	public Parsec subtract(Parsec other) {
		return new Parsec(this.quantity.subtract(other.quantity));
	}

	public Parsec multiply(Parsec multiplicand) {
		return new Parsec(this.quantity.multiply(multiplicand.quantity));
	}

	public Parsec multiply(Parsec numerator, Parsec denominator) {
		return new Parsec(
				numerator.quantity.divide(denominator.quantity, 6, RoundingMode.HALF_UP).multiply(this.quantity));
	}

	public Parsec divide(Parsec divisor) {
		return new Parsec(this.quantity.divide(divisor.quantity));
	}

	public Parsec sqrt() {
		return new Parsec(this.quantity.sqrt(new MathContext(this.quantity.precision())));
	}

	public String toPlainString() {
		return Long.toString(this.quantity.movePointRight(3).longValue());
	}

	public int roundUp() {
		return this.quantity.setScale(0, RoundingMode.UP).intValue();
	}

	@Override
	public int compareTo(Parsec o) {
		return this.quantity.compareTo(o.quantity);
	}

}
