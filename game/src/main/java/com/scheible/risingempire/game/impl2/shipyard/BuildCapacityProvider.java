
package com.scheible.risingempire.game.impl2.shipyard;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public interface BuildCapacityProvider {

	Credit buildCapacity(Player player, Position system);

}
