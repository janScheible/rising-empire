package com.scheible.risingempire.game.api.view.fleet;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import com.scheible.risingempire.util.jdk.Long2;

/**
 * Unique (if enforced somehow from the outside) identifier of a fleet. It is a hex number
 * in the interval [0x123456, 0xFFFFFF).
 *
 * @author sj
 */
public record FleetId(String value) {

	private static final long MIN = 1_193_046;

	private static final long MAX = 16_777_215;

	/**
	 * @throws IllegalArgumentException if the string does not contain a valid id.
	 */
	public FleetId(String value) {
		this.value = Long2.tryParseLong(value, 16)
			.filter(n -> n >= MIN && n < MAX)
			.map(n -> value)
			.orElseThrow(() -> new IllegalArgumentException(
					String.format(Locale.ROOT, "Id must be a valid hex number in the interval [0x%s, 0x%s)!",
							Long.toHexString(MIN), Long.toHexString(MAX))));
	}

	public static FleetId createRandom() {
		return new FleetId(Long.toHexString(ThreadLocalRandom.current().nextLong(MIN, MAX)));
	}

}
