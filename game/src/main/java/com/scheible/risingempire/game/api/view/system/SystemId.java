package com.scheible.risingempire.game.api.view.system;

import java.util.Objects;

import com.scheible.risingempire.game.api.view.colony.ColonyId;

/**
 * @author sj
 */
public class SystemId {

	private final String value;

	public SystemId(String value) {
		this.value = value;
	}

	public static SystemId fromColonyId(ColonyId colonyId) {
		return new SystemId(colonyId.getValue());
	}

	public ColonyId toColonyId() {
		return new ColonyId(this.value);
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			SystemId other = (SystemId) obj;
			return Objects.equals(this.value, other.value);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.value);
	}

	@Override
	public String toString() {
		return this.value;
	}

}
