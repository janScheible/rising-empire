package com.scheible.risingempire.web.game.message.server;

import com.scheible.risingempire.game.common.Player;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sj
 */
public class TurnInfoMessage {
    
    private final Map<String, Boolean> status = new HashMap<>(); // <nationName, boolean>
	
	public TurnInfoMessage(Map<Player, Boolean> status) {
		for(Player player : status.keySet()) {
			this.status.put(player.getNation(), status.get(player));
		}
	}
}
