package com.scheible.risingempire.game.impl.ship;

import java.util.Objects;

/**
 * @author sj
 */
public class BattleScanner extends AbstractSpecial {

	public BattleScanner() {
		super("Battle Scanner");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj != null && obj.getClass().equals(getClass())) {
			BattleScanner other = (BattleScanner) obj;
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
