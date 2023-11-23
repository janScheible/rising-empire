package com.scheible.risingempire.game.impl.ship;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author sj
 */
public class Missile extends AbstractWeapon {

	public enum RackSize {

		TWO(2), FIVE(5);

		final int size;

		RackSize(int size) {
			this.size = size;
		}

		public int getSize() {
			return this.size;
		}

	}

	private final RackSize rackSize;

	public Missile(String name, Damage damage, RackSize rackSize) {
		super(name, damage);
		this.rackSize = rackSize;
	}

	public RackSize getRackSize() {
		return this.rackSize;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj != null && obj.getClass().equals(getClass())) {
			Missile other = (Missile) obj;
			return Objects.equals(this.name, other.name) && Objects.equals(this.damage, other.damage)
					&& Objects.equals(this.rackSize, other.rackSize);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.damage, this.rackSize);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", "Missile[", "]").add("name='" + this.name + "'")
			.add("damage=" + this.damage)
			.add("racks=" + this.rackSize.size)
			.toString();
	}

}
