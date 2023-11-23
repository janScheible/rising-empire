package com.scheible.risingempire.game.api.view.tech;

import java.util.Objects;

/**
 * @author sj
 */
public class TechId {

	private final String value;

	public TechId(String value) {
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
			TechId other = (TechId) obj;
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
