package com.scheible.risingempire.game.impl2.colonization;

import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;

/**
 * @author sj
 */
public record SpaceDock(ShipClassId current, Construction construction) {

	SpaceDock withCurrent(ShipClassId current) {
		return new SpaceDock(current, this.construction);
	}

	public record Construction(ShipClassId underConstruction, Credit invest) {

	}

}
