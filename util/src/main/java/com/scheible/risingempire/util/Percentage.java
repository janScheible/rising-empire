package com.scheible.risingempire.util;

/**
 * @author sj
 */
public record Percentage(int value) {

	public Percentage {
		if (value < 0 || value > 100) {
			throw new IllegalArgumentException("A value of " + value + " is illegal for a percentage (0..100).");
		}

	}

}
