package com.scheible.risingempire.web.join;

import com.scheible.risingempire.game.common.Player;
import com.scheible.risingempire.web.join.message.server.PlayerEntry;
import com.scheible.risingempire.web.appearance.AvailablePlayer;
import com.scheible.risingempire.web.game.GameHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author sj
 */
@Component
public class PlayerManager {
	
	private final Object LOCK = new Object();	
	private final static Map<String, Player> idPlayerMapping = new ConcurrentHashMap<>();
	
	@Autowired
	GameHolder gameHolder;

	public List<PlayerEntry> getPlayerEntries() {
		List<PlayerEntry> entries = new ArrayList<>();
		
		for(AvailablePlayer availablePlayer : AvailablePlayer.values()) {
			Player player = new Player(availablePlayer.getLeaderName(), availablePlayer.getNation());
			
			PlayerEntry.State state = PlayerEntry.State.NON_PARTICIPATING;
			if(idPlayerMapping.containsValue(player)) {
				state = PlayerEntry.State.ACTIVE;
			} else if(gameHolder.getPlayers().contains(player)) {
				state = gameHolder.isAi(player) ? PlayerEntry.State.AI : PlayerEntry.State.DETACHED;
			}
			
			entries.add(PlayerEntry.create(player.getName(), player.getNation(), state));
		}
		
		return entries;
	}

	public void addPlayer(String sessionId, String username) {
		synchronized(LOCK) {
			Player player = PlayerHelper.resolvePlayer(username);

			if(gameHolder.getPlayers().contains(player)) {
				if(idPlayerMapping.containsValue(player)) {
					throw new IllegalStateException("Player " + player + " is already assigned to a session!");
				}
			} else {
				gameHolder.register(player);
			}

			idPlayerMapping.put(sessionId, player);		
		}
	}
	
	public void addAi(Player player) {
		synchronized(LOCK) {
			gameHolder.addAi(player);
		}
	}	

	public void removePlayer(String sessionId) {
		synchronized(LOCK) {
			idPlayerMapping.remove(sessionId);
		}
	}
}
