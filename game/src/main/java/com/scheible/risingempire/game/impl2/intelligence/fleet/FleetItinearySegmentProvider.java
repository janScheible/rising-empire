package com.scheible.risingempire.game.impl2.intelligence.fleet;

import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public interface FleetItinearySegmentProvider {

	Optional<FleetItinerarySegment> fleetItinerarySegment(Player player, Position fleet);

	record FleetItinerarySegment(Position origin, Position destination) {

	}

}
