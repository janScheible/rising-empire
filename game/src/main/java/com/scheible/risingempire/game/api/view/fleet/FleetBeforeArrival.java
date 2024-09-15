package com.scheible.risingempire.game.api.view.fleet;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection;

/**
 * @author sj
 */
@StagedRecordBuilder
public record FleetBeforeArrival(FleetId id, HorizontalDirection horizontalDirection, int speed, Location location,
		boolean justLeaving) {

	public FleetBeforeArrival {
	}
}
