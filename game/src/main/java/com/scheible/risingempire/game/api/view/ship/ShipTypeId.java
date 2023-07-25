package com.scheible.risingempire.game.api.view.ship;

import java.util.Objects;

/**
 *
 * @author sj
 */
public class ShipTypeId {

	private final String value;

	public ShipTypeId(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj != null && getClass().equals(obj.getClass())) {
			final ShipTypeId other = (ShipTypeId) obj;
			return Objects.equals(value, other.value);
		} else {
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
