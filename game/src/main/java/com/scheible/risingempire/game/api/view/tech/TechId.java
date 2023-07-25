package com.scheible.risingempire.game.api.view.tech;

import java.util.Objects;

/**
 *
 * @author sj
 */
public class TechId {

	private final String value;

	public TechId(final String value) {
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
			final TechId other = (TechId) obj;
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
