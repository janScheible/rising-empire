package com.scheible.risingempire.game.impl2.view;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public class SystemIdMapper {

	public static SystemId toSystemId(Position position) {
		return new SystemId("s" + position.toPlainString());
	}

	public static Position fromSystemId(SystemId systemId) {
		return Position.fromPlainString(systemId.value().substring(1));
	}

}
