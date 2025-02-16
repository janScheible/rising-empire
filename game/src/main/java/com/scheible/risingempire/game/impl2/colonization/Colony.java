package com.scheible.risingempire.game.impl2.colonization;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Population;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.colonization.ColonyBuilder.PlayerStage;

/**
 * @author sj
 */
@StagedRecordBuilder
public record Colony(Player player, Position position, SpaceDock spaceDock,
		Population population) implements ColonyBuilder.With {

	public static PlayerStage builder() {
		return ColonyBuilder.builder();
	}

}
