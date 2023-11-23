package com.scheible.risingempire.game.impl.ship;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author sj
 */
public class BeamWeapon extends AbstractWeapon {

	public BeamWeapon(String name, Damage damage) {
		super(name, damage);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj != null && obj.getClass().equals(getClass())) {
			BeamWeapon other = (BeamWeapon) obj;
			return Objects.equals(this.name, other.name) && Objects.equals(this.damage, other.damage);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.damage);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", "BeamWeapon[", "]").add("name='" + this.name + "'")
			.add("damage=" + this.damage)
			.toString();
	}

}
