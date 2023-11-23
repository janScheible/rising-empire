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
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj != null && obj.getClass().equals(getClass())) {
			ColonyBase other = (ColonyBase) obj;
			return Objects.equals(this.name, other.name);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name);
	}

}
