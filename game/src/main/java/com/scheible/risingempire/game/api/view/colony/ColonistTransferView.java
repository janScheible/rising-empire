package com.scheible.risingempire.game.api.view.colony;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.view.colony.ColonistTransferViewBuilder.DesinationStage;

/**
 * @author sj
 */
@StagedRecordBuilder
public record ColonistTransferView(ColonyId desination, int colonists) {

	public static DesinationStage builder() {
		return ColonistTransferViewBuilder.builder();
	}
}
