package com.scheible.risingempire.game.impl.fleet;

import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;

/**
 * @author sj
 */
public interface FleetManager {

	void deployFleet(Player player, FleetId fleetId, SystemId destinationId, Map<ShipTypeId, Integer> ships);

	void colonizeSystem(Player player, FleetId fleetId, boolean skip);

	boolean canColonize(FleetId fleetId);

	boolean hasColonizeCommand(Player player, SystemId systemId, FleetId fleetId);

	void annexSystem(Player player, FleetId fleetId, boolean skip);

	boolean canAnnex(FleetId fleetId);

	boolean hasAnnexCommand(Player player, SystemId systemId, FleetId fleetId);

	Optional<Integer> getSiegeProgress(FleetId fleetId);

	Optional<Integer> calcEta(Player player, FleetId fleetId, SystemId destinationId, Map<ShipTypeId, Integer> ships);

	Optional<Integer> calcTranportColonistsEta(Player player, SystemId originId, SystemId destinationId);

	void transferColonists(Player player, ColonyId originId, ColonyId destinationId, int colonists);

	void relocateShips(Player player, ColonyId originId, ColonyId destinationId);

	SystemId getClosest(FleetId fleetId);

}
