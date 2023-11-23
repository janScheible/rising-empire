package com.scheible.risingempire.game.impl.ship;

import java.util.Objects;

/**
 * @author sj
 */
public class WeaponSlot {

	private final int count;

	private final AbstractWeapon weapon;

	public WeaponSlot(int count, AbstractWeapon weapon) {
		this.weapon = weapon;
		this.count = count;
	}

	public AbstractWeapon getWeapon() {
		return this.weapon;
	}

	public int getCount() {
		return this.count;
	}

	public static boolean isNotEmpty(WeaponSlot weaponSlot) {
		return weaponSlot.weapon != null & weaponSlot.count > 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj != null && obj.getClass().equals(getClass())) {
			WeaponSlot other = (WeaponSlot) obj;
			return Objects.equals(this.count, other.count) && Objects.equals(this.weapon, other.weapon);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.count, this.weapon);
	}

	@Override
	public String toString() {
		return this.count + " " + this.weapon;
	}

}
