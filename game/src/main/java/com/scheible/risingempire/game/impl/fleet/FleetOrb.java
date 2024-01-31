package com.scheible.risingempire.game.impl.fleet;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.impl.universe.Orb;

/**
 * @author sj
 */
public interface FleetOrb extends Orb<FleetId> {

	Player getPlayer();

}
