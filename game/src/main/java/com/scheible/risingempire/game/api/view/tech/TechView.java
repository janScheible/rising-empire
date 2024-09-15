package com.scheible.risingempire.game.api.view.tech;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;

/**
 * @author sj
 */
@StagedRecordBuilder
public record TechView(TechId id, String name, String description) {

	public TechView {
	}

}
