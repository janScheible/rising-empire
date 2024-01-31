package com.scheible.risingempire.game.impl.game;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;

/**
 * @author sj
 */
record AnnexCommand(Player player, SystemId systemId, FleetId fleetId) {

}
