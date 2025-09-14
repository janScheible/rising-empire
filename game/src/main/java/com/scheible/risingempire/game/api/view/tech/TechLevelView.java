package com.scheible.risingempire.game.api.view.tech;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.view.tech.TechLevelViewBuilder.FactoryStage;

/**
 * @author sj
 */
@StagedRecordBuilder
public record TechLevelView(int factory, int ship, int research) {

	public static FactoryStage builder() {
		return TechLevelViewBuilder.builder();
	}
}
