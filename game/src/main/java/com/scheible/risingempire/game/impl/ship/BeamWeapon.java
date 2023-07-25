package com.scheible.risingempire.game.impl.ship;

import java.util.Objects;
import java.util.StringJoiner;

/**
 *
 * @author sj
 */
public class BeamWeapon extends AbstractWeapon {

	public BeamWeapon(final String name, final Damage damage) {
		super(name, damage);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && obj.getClass().equals(getClass())) {
			final BeamWeapon other = (BeamWeapon) obj;
			return Objects.equals(name, other.name) && Objects.equals(damage, other.damage);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, damage);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", "BeamWeapon[", "]").add("name='" + name + "'").add("damage=" + damage).toString();
	}
}
