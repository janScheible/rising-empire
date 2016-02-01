package com.scheible.risingempire.web.game;

import com.scheible.risingempire.game.common.Game;
import com.scheible.risingempire.game.common.GameFactory;
import com.scheible.risingempire.game.common.command.Command;
import com.scheible.risingempire.game.common.view.View;
import com.scheible.risingempire.game.common.Player;
import com.scheible.risingempire.game.common.event.Event;
import com.scheible.risingempire.web.appearance.AvailablePlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 *
 * @author sj
 */
@Component
public class GameHolder {
	
	private static Game game;
	
	@PostConstruct
	private void init() {
		List<Player.Prototype> playerPrototypes = new ArrayList<>();
		
		for(AvailablePlayer availablePlayer : AvailablePlayer.values()) {
			playerPrototypes.add(new Player.Prototype(availablePlayer.getLeaderName(), 
					availablePlayer.getNation(), availablePlayer.getHomeStar()));
		}
		
		game = GameFactory.create(playerPrototypes);
	}
	
	public void register(Player player) {
		synchronized(game) {
			game.register(player);
		}
	}
	
	public void addAi(Player player) {
		synchronized(game) {
			game.addAi(player);
		}
	}	
	
	public List<Player> getPlayers() {
		return game.getPlayers();
	}		
	
	public boolean isAi(Player player) {
		return game.isAi(player);
	}	

	public boolean process(Player player, List<Command> commands) {
		synchronized(game) {
			return game.process(player, commands);
		}
	}

	public Map<Player, View> getViews() {
		synchronized(game) {
			return game.getViews();
		}
	}

	public int getTurn() {
		return game.getTurn();
	}
	
	public  Map<Player, Boolean> getTurnFinishInfo() {
		return game.getTurnFinishInfo();
	}	
}
