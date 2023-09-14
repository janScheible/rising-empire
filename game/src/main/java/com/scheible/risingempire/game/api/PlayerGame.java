package com.scheible.risingempire.game.api;

import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.api.view.universe.Player;

/**
 *
 * @author sj
 */
public interface PlayerGame {

	//
	// queries
	//

	GameView getView();

	Optional<Integer> calcEta(FleetId fleetId, SystemId destinationId, Map<ShipTypeId, Integer> ships);

	Player getPlayer();

	//
	// commands
	//

	TurnStatus finishTurn();

	void nextShipType(ColonyId colonyId);

	void colonizeSystem(FleetId fleetId);

	void annexSystem(FleetId fleetId);

	void deployFleet(FleetId fleetId, SystemId destinationId, Map<ShipTypeId, Integer> ships);

	void selectTech(TechId techId);
}
