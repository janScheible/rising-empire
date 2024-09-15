package com.scheible.risingempire.game.api.view.system;

import com.scheible.risingempire.game.api.view.colony.ColonyId;

/**
 * @author sj
 */
public record SystemId(String value) {

	public static SystemId fromColonyId(ColonyId colonyId) {
		return new SystemId(colonyId.value());
	}

	public ColonyId toColonyId() {
		return new ColonyId(this.value);
	}

}
