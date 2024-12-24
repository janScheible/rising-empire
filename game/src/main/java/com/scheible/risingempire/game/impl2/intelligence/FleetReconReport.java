package com.scheible.risingempire.game.impl2.intelligence;

import java.util.Optional;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public record FleetReconReport(boolean scanned, Optional<ItineraryReconReport> itineraryReconReport) {

	public record ItineraryReconReport(Optional<Position> previousLocation, Optional<SystemId> source,
			Optional<SystemId> destination) {

	}
}
