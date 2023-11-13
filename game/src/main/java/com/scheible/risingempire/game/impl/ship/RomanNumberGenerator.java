package com.scheible.risingempire.game.impl.ship;

/**
 * @author sj
 */
class RomanNumberGenerator {

	private static final String[] LOOKUP_ARRAY = { "", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX" };

	static String getNumber(final int value) {
		return LOOKUP_ARRAY[value];
	}

}
