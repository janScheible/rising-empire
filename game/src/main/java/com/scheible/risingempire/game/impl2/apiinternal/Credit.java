package com.scheible.risingempire.game.impl2.apiinternal;

/**
 * Billion credits.
 *
 * @author sj
 */
public record Credit(int quantity) {

	public Credit add(Credit augend) {
		return new Credit(this.quantity + augend.quantity);
	}

	public int integerDivide(Credit divisor) {
		return this.quantity / divisor.quantity;
	}

	public int divideRoundUp(Credit divisor) {
		return Math.ceilDiv(this.quantity, divisor.quantity);
	}

	public Credit modulo(Credit divisor) {
		return new Credit(this.quantity % divisor.quantity);
	}

	public Credit subtract(Credit subtrahend) {
		return new Credit(this.quantity - subtrahend.quantity);
	}

	public boolean lessThan(Credit other) {
		return Integer.compare(this.quantity, other.quantity) < 0;
	}

	public boolean greaterThan(Credit other) {
		return Integer.compare(this.quantity, other.quantity) > 0;
	}

}
