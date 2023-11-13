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

		RackSize(final int size) {
			this.size = size;
		}

		public int getSize() {
			return size;
		}

	}

	private final RackSize rackSize;

	public Missile(final String name, final Damage damage, final RackSize rackSize) {
		super(name, damage);
		this.rackSize = rackSize;
	}

	public RackSize getRackSize() {
		return rackSize;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj != null && obj.getClass().equals(getClass())) {
			final Missile other = (Missile) obj;
			return Objects.equals(name, other.name) && Objects.equals(damage, other.damage)
					&& Objects.equals(rackSize, other.rackSize);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, damage, rackSize);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", "Missile[", "]").add("name='" + name + "'")
			.add("damage=" + damage)
			.add("racks=" + rackSize.size)
			.toString();
	}

}
