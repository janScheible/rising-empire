package com.scheible.risingempire.game.impl.ship;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author sj
 */
public abstract class AbstractWeapon {

	protected final String name;

	protected final Damage damage;

	public AbstractWeapon(String name, Damage damage) {
		this.name = name;
		this.damage = damage;
	}

	public String getName() {
		return this.name;
	}

	public Damage getDamage() {
		return this.damage;
	}

	public static class Damage {

		private final int min;

		private final int max;

		public Damage(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public Damage(int damage) {
			this.min = damage;
			this.max = damage;
		}

		public int getMax() {
			return this.max;
		}

		public int getMin() {
			return this.min;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			else if (obj != null && obj.getClass().equals(getClass())) {
				Damage other = (Damage) obj;
				return Objects.equals(this.min, other.min) && Objects.equals(this.max, other.max);
			}
			else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.min, this.max);
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", "Damage[", "]").add("min=" + this.min).add("max=" + this.max).toString();
		}

	}

}
