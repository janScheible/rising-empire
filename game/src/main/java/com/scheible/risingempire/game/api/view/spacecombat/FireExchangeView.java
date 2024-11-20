package com.scheible.risingempire.game.api.view.spacecombat;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.view.spacecombat.FireExchangeViewBuilder.RoundStage;

/**
 * @author sj
 */
@StagedRecordBuilder
public record FireExchangeView(int round, int lostHitPoints, int damage, int count) {

	public static RoundStage builder() {
		return FireExchangeViewBuilder.builder();
	}
}
