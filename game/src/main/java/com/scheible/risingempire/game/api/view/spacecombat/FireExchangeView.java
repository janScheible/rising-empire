package com.scheible.risingempire.game.api.view.spacecombat;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;

/**
 * @author sj
 */
@StagedRecordBuilder
public record FireExchangeView(int round, int lostHitPoints, int damage, int count) {

	public FireExchangeView {

	}
}
