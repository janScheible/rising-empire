package com.scheible.risingempire.game.api.view.ship;

import java.util.Objects;

/**
 * @author sj
 */
public class ShipTypeId {

	private final String value;

	public ShipTypeId(String value) {
		this.value = value;
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
			ShipTypeId other = (ShipTypeId) obj;
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
