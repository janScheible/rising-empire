package com.scheible.risingempire.util.jdk;

import java.util.Optional;

/**
 *
 * @author sj
 */
public class Long2 {

	public static Optional<Long> tryParseLong(final String s, final int radix) {
		try {
			return Optional.of(Long.parseLong(s, radix));
		} catch (final NumberFormatException ex) {
			return Optional.empty();
		}
	}

	public static Optional<Long> tryParseLong(final String s) {
		return tryParseLong(s, 10);
	}
}
