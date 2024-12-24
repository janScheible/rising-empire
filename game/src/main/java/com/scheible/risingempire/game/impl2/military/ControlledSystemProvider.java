package com.scheible.risingempire.game.impl2.military;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public interface ControlledSystemProvider {

	Set<Position> controlledSystems(Player player);

}
