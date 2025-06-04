package com.scheible.risingempire.game.impl2.game;

import java.util.List;
import java.util.Map;

import com.scheible.risingempire.game.api.GameFactory.Savegame;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.common.Command;

/**
 * @author sj
 */
public record Savegame2Impl(GameOptions gameOptions, long seed,
		Map<Round, Map<Player, List<Command>>> commands) implements Savegame {

}
