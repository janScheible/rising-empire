package com.scheible.risingempire.game.common;

import com.scheible.risingempire.game.common.command.Command;
import com.scheible.risingempire.game.common.event.Event;
import com.scheible.risingempire.game.common.view.View;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sj
 */
public interface Game {

	void register(Player player);
	void addAi(Player player);

	List<Player> getPlayers();
	boolean isAi(Player player);

	boolean process(Player player, List<Command> commands);

	Map<Player, View> getViews();

	int getTurn();

	Map<Player, Boolean> getTurnFinishInfo();	
}
