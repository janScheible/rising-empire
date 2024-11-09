package com.scheible.risingempire.game.impl2.game;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
class SystemIdMapper {

	static SystemId toSystemId(Position position) {
		return new SystemId("s" + position.toPlainString());
	}

	static Position fromSystemId(SystemId systemId) {
		return Position.fromPlainString(systemId.value().substring(1));
	}

}
