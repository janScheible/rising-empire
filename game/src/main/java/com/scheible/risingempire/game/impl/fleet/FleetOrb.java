package com.scheible.risingempire.game.impl.fleet;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.universe.Orb;
import com.scheible.risingempire.game.api.view.universe.Player;

/**
 *
 * @author sj
 */
public interface FleetOrb extends Orb<FleetId> {

	Player getPlayer();
}
