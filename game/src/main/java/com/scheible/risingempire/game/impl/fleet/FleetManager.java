package com.scheible.risingempire.game.impl.fleet;

import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.api.view.system.SystemId;

/**
 * @author sj
 */
public interface FleetManager {

	void deployFleet(Player player, FleetId fleetId, SystemId destinationId, ShipsView ships);

	void colonizeSystem(Player player, FleetId fleetId, boolean skip);

	boolean canColonize(FleetId fleetId);

	boolean hasColonizeCommand(Player player, SystemId systemId, FleetId fleetId);

	void annexSystem(Player player, FleetId fleetId, boolean skip);

	boolean canAnnex(FleetId fleetId);

	boolean hasAnnexCommand(Player player, SystemId systemId, FleetId fleetId);

	Optional<Integer> getSiegeProgress(FleetId fleetId);

	Optional<Integer> calcEta(Player player, FleetId fleetId, SystemId destinationId, ShipsView ships);

	Optional<Integer> calcTranportColonistsEta(Player player, SystemId originId, SystemId destinationId);

	void transferColonists(Player player, ColonyId originId, ColonyId destinationId, int colonists);

	void relocateShips(Player player, ColonyId originId, ColonyId destinationId);

	SystemId getClosest(FleetId fleetId);

}
