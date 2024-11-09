package com.scheible.risingempire.game.impl2.apiinternal;

import java.util.Optional;

/**
 * @author sj
 */
public record Round(int quantity) {

	public Round {
		if (quantity <= 0) {
			throw new IllegalArgumentException("Round must be larger than 0 but was " + quantity + ".");
		}
	}

	public Optional<Round> previous() {
		if (this.quantity > 1) {
			return Optional.of(new Round(this.quantity - 1));
		}
		else {
			return Optional.empty();
		}
	}

	public Round next() {
		return new Round(this.quantity + 1);
	}

}
