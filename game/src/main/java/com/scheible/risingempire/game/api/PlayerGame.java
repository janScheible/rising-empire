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
	// Queries to get information about the game from the player's point of view.
	//

	GameView getView();

	Optional<Integer> calcEta(FleetId fleetId, SystemId destinationId, Map<ShipTypeId, Integer> ships);

	Player getPlayer();

	//
	// Commands of the player that will be executed at end of turn.
	//

	void nextShipType(ColonyId colonyId);

	void colonizeSystem(FleetId fleetId, boolean skip);

	void annexSystem(FleetId fleetId, boolean skip);

	void deployFleet(FleetId fleetId, SystemId destinationId, Map<ShipTypeId, Integer> ships);

	void selectTech(TechId techId);

	//
	// End player turn. Round ends as soon as all players have finished their turns.
	//

	TurnStatus finishTurn();
}
