package com.scheible.risingempire.game.api.view;

import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;

/**
 *
 * @author sj
 */
public interface FleetManager {

	void deployFleet(Player player, FleetId fleetId, SystemId destinationId, Map<ShipTypeId, Integer> ships);

	void colonizeSystem(Player player, FleetId fleetId);

	Optional<Integer> calcEta(Player player, FleetId fleetId, SystemId destinationId, Map<ShipTypeId, Integer> ships);

	SystemId getClosest(FleetId fleetId);
}
