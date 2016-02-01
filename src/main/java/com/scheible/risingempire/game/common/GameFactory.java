package com.scheible.risingempire.game.common;

import com.scheible.risingempire.game.common.command.Command;
import com.scheible.risingempire.game.common.view.View;
import com.scheible.risingempire.game.core.Leader;
import com.scheible.risingempire.game.core.Universe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sj
 */
public class GameFactory {
	
	private static class GameFacade implements Game {
		
		private final Universe universe;
		private final List<Player.Prototype> playerPrototypes;

		private GameFacade(List<Player.Prototype> playerPrototypes) {
			this.playerPrototypes = playerPrototypes;
			universe = new Universe();
		}

		@Override
		public void register(Player player) {
			universe.register(resolve(player));
		}
		
		@Override
		public void addAi(Player player) {
			universe.addAi(resolve(player));
		}
		
		private Leader resolve(Player player) {
			for(Player.Prototype playerPrototype : playerPrototypes) {
				if(playerPrototype.getName().equals(player.getName()) && playerPrototype.getNation().equals(player.getNation())) {
					return new Leader(player.getName(), player.getNation(), playerPrototype.getHomeStar());
				}
			}
			
			throw new IllegalStateException("The player '" + player.getName() + "' with the nation '" + player.getNation() + "' does not exist!");
		}

		@Override
		public List<Player> getPlayers() {
			List<Player> players = new ArrayList<>();
			
			for(Leader leader : universe.getLeaders()) {
				players.add(new Player(leader.getName(), leader.getNation()));
			}
			return players;
		}
		
		@Override
		public boolean isAi(Player player) {
			return universe.isAi(resolve(player));
		}		

		@Override
		public boolean process(Player player, List<Command> commands) {
			for(Leader leader : universe.getLeaders()) {
				if(leader.getName().equals(player.getName()) && leader.getNation().equals(player.getNation())) {
					return universe.process(leader, commands);
				}
			}
			
			throw new IllegalStateException("The player '" + player.getName() + "' with the nation '" + player.getNation() + "' does not exist!");
		}

		@Override
		public Map<Player, View> getViews() {
			Map<Leader, View> views = universe.getViews();
			Map<Player, View> result = new HashMap<>();

			for (Leader leader : views.keySet()) {
				result.put(new Player(leader.getName(), leader.getNation()), views.get(leader));
			}

			return result;
		}

		@Override
		public int getTurn() {
			return universe.getTurn();
		}

		@Override
		public Map<Player, Boolean> getTurnFinishInfo() {
			 Map<Leader, Boolean> turnFinishInfo = universe.getTurnFinishInfo();
			 Map<Player, Boolean> result = new HashMap<>();
			 
			 for(Leader leader : turnFinishInfo.keySet()) {
				 result.put(new Player(leader.getName(), leader.getNation()), turnFinishInfo.get(leader));
			 }
			 
			 return result;
		}
	}
	
	public static Game create(List<Player.Prototype> playerPrototypes) {
		return new GameFacade(playerPrototypes);
	}	
}
