package com.scheible.risingempire.game.impl.ship;

import java.util.Objects;

/**
 *
 * @author sj
 */
public class ReserveTanks extends AbstractSpecial {

	public ReserveTanks() {
		super("Reserve Tanks");
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && obj.getClass().equals(getClass())) {
			final ReserveTanks other = (ReserveTanks) obj;
			return Objects.equals(name, other.name);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
