package com.scheible.risingempire.game.impl2.colonization;

import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;

/**
 * @author sj
 */
public record SpaceDock(ShipClassId current, SpaceDockOutput output, ConstructionProgress progress) {

	static SpaceDock UNINITIALIZED = new SpaceDock(null, null, null);

	public record SpaceDockOutput(Rounds duration, int nextRoundCount) {

	}

	public record ConstructionProgress(ShipClassId underConstruction, Credit invest) {

		public Credit build(ShipClassId current, Credit invest) {
			if (current.equals(this.underConstruction)) {
				return this.invest.add(invest);
			}
			else {
				return invest;
			}
		}

	}

}
