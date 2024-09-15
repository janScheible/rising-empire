package com.scheible.risingempire.game.api.view.ship;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;

/**
 * @author sj
 */
@StagedRecordBuilder
public record ShipTypeView(ShipTypeId id, int index, String name, ShipSize size, int look) {

	public ShipTypeView {
	}

}
