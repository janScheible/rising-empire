package com.scheible.risingempire.game.impl.fleet;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.impl.ship.ShipDesignProvider;
import com.scheible.risingempire.game.impl.spacecombat.SpaceCombat;

/**
 *
 * @author sj
 */
@FunctionalInterface
public interface SpaceCombatResolver {

	SpaceCombat resolve(SystemId systemId, OrbitingFleet defending, DeployedFleet attacking,
			ShipDesignProvider shipDesignProvider);
}
