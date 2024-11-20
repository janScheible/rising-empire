package com.scheible.risingempire.game.api.view.tech;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.view.tech.TechViewBuilder.IdStage;

/**
 * @author sj
 */
@StagedRecordBuilder
public record TechView(TechId id, String name, String description) {

	public static IdStage builder() {
		return TechViewBuilder.builder();
	}
}
