package com.scheible.risingempire.game.impl.ship;

import java.util.Objects;

/**
 * @author sj
 */
public class WeaponSlot {

	private final int count;

	private final AbstractWeapon weapon;

	public WeaponSlot(final int count, final AbstractWeapon weapon) {
		this.weapon = weapon;
		this.count = count;
	}

	public AbstractWeapon getWeapon() {
		return weapon;
	}

	public int getCount() {
		return count;
	}

	public static boolean isNotEmpty(final WeaponSlot weaponSlot) {
		return weaponSlot.weapon != null & weaponSlot.count > 0;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj != null && obj.getClass().equals(getClass())) {
			final WeaponSlot other = (WeaponSlot) obj;
			return Objects.equals(count, other.count) && Objects.equals(weapon, other.weapon);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(count, weapon);
	}

	@Override
	public String toString() {
		return count + " " + weapon;
	}

}
