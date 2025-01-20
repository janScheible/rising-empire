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

	public Credit modulo(Credit divisor) {
		return new Credit(this.quantity % divisor.quantity);
	}

}
