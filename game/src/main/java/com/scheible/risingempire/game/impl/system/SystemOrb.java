package com.scheible.risingempire.game.impl.system;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.impl.universe.Orb;

/**
 * @author sj
 */
public interface SystemOrb extends Orb<SystemId> {

	String getName();

}
