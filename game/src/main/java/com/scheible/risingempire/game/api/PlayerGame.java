package com.scheible.risingempire.game.api;

import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechId;

/**
 * @author sj
 */
public interface PlayerGame {

	//
	// Queries to get information about the game from the player's point of view.
	//

	GameView getView();

	Optional<Integer> calcEta(FleetId fleetId, SystemId destinationId, ShipsView ships);

	Optional<Integer> calcTranportColonistsEta(SystemId originId, SystemId destinationId);

	Player getPlayer();

	//
	// Commands of the player that change the state of the game (some have immediate
	// effect, others will be enqueued and executed at end of turn).
	//

	void nextShipType(ColonyId colonyId);

	void transferColonists(ColonyId originId, ColonyId destinationId, int colonists);

	void relocateShips(ColonyId originId, ColonyId destinationId);

	void colonizeSystem(SystemId systemId, FleetId fleetId, boolean skip);

	void annexSystem(ColonyId colonyId, FleetId fleetId, boolean skip);

	void deployFleet(FleetId fleetId, SystemId destinationId, ShipsView ships);

	void selectTech(TechId techId);

	//
	// End player turn. Round ends as soon as all players have finished their turns.
	//

	TurnStatus finishTurn();

}
