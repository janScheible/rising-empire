package com.scheible.risingempire.game.impl.ship;

import java.util.Objects;

/**
 * @author sj
 */
public class ColonyBase extends AbstractSpecial {

	public ColonyBase() {
		super("Colony Base");
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj != null && obj.getClass().equals(getClass())) {
			final ColonyBase other = (ColonyBase) obj;
			return Objects.equals(name, other.name);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}
