package com.scheible.risingempire.game.ai;

import com.scheible.risingempire.game.common.command.Command;
import com.scheible.risingempire.game.common.view.View;
import com.scheible.risingempire.game.core.Leader;
import java.util.List;

/**
 *
 * @author sj
 */
public interface Ai {

	List<Command> think(Leader leader, View view);	
}
