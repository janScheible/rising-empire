package com.scheible.risingempire.game.api.view.fleet;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import com.scheible.risingempire.util.jdk.Long2;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Unique (if enforced somehow from the outside) identifier of a fleet. It is a hex number
 * in the interval [0x123456, 0xFFFFFF).
 *
 * @author sj
 */
public class FleetId {

	private static final long MIN = 1193046;

	private static final long MAX = 16777215;

	private final String value;

	/**
	 * @throws IllegalArgumentException if the string does not contain a valid id.
	 */
	public FleetId(final String value) {
		this.value = Long2.tryParseLong(value, 16)
			.filter(n -> n >= MIN && n < MAX)
			.map(n -> value)
			.orElseThrow(() -> new IllegalArgumentException(
					String.format("Id must be a valid hex number in the interval [0x%s, 0x%s)!", Long.toHexString(MIN),
							Long.toHexString(MAX))));
	}

	@SuppressFBWarnings(value = "PREDICTABLE_RANDOM", justification = "Uniqueness has to be enfored from the outside.")
	public static FleetId createRandom() {
		return new FleetId(Long.toHexString(ThreadLocalRandom.current().nextLong(MIN, MAX)));
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			final FleetId other = (FleetId) obj;
			return Objects.equals(value, other.value);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return value;
	}

}
