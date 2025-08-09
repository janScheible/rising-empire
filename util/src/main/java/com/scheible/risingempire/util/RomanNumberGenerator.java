package com.scheible.risingempire.util;

/**
 * @author sj
 */
public class RomanNumberGenerator {

	private static final String[] THOUSANDS = { "", "M", "MM", "MMM" };

	private static final String[] HUNDREDS = { "", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM" };

	private static final String[] TENS = { "", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC" };

	private static final String[] UNITS = { "", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX" };

	public static String getNumber(int n) {
		return THOUSANDS[n / 1000] + HUNDREDS[(n % 1000) / 100] + TENS[(n % 100) / 10] + UNITS[n % 10];
	}

}
