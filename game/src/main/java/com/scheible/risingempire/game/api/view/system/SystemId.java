package com.scheible.risingempire.game.api.view.system;

import java.util.Objects;

import com.scheible.risingempire.game.api.view.colony.ColonyId;

/**
 *
 * @author sj
 */
public class SystemId {

	private final String value;

	public SystemId(final String value) {
		this.value = value;
	}

	public static SystemId fromColonyId(final ColonyId colonyId) {
		return new SystemId(colonyId.getValue());
	}

	public ColonyId toColonyId() {
		return new ColonyId(value);
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj != null && getClass().equals(obj.getClass())) {
			final SystemId other = (SystemId) obj;
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
