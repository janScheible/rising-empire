package com.scheible.risingempire.game.impl.ship;

import java.util.Objects;
import java.util.StringJoiner;

/**
 *
 * @author sj
 */
public abstract class AbstractWeapon {

	public static class Damage {

		private final int min;
		private final int max;

		public Damage(final int min, final int max) {
			this.min = min;
			this.max = max;
		}

		public Damage(final int damage) {
			this.min = damage;
			this.max = damage;
		}

		public int getMax() {
			return max;
		}

		public int getMin() {
			return min;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			} else if (obj != null && obj.getClass().equals(getClass())) {
				final Damage other = (Damage) obj;
				return Objects.equals(min, other.min) && Objects.equals(max, other.max);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(min, max);
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", "Damage[", "]").add("min=" + min).add("max=" + max).toString();
		}
	}

	protected final String name;
	protected final Damage damage;

	public AbstractWeapon(final String name, final Damage damage) {
		this.name = name;
		this.damage = damage;
	}

	public String getName() {
		return name;
	}

	public Damage getDamage() {
		return damage;
	}
}
