
package com.scheible.risingempire.game.api.view.colony;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.view.colony.SpaceDockViewBuilder.CurrentStage;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;

/**
 * @author sj
 */
@StagedRecordBuilder
public record SpaceDockView(ShipTypeView current, int count) {

	public static CurrentStage builder() {
		return SpaceDockViewBuilder.builder();
	}

}
