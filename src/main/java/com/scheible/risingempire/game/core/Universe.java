package com.scheible.risingempire.game.core;

import com.scheible.risingempire.game.common.command.Command;
import com.scheible.risingempire.game.core.fleet.Fleet;
import com.scheible.risingempire.game.core.star.Colony;
import com.scheible.risingempire.game.core.star.Star;
import com.scheible.risingempire.game.common.view.View;
import com.scheible.risingempire.game.ai.Ai;
import com.scheible.risingempire.game.ai.FleetSendingAi;
import com.scheible.risingempire.game.core.event.Event;
import com.scheible.risingempire.game.core.event.RandomMessageEvent;
import com.scheible.risingempire.game.core.fleet.Fleets;
import com.scheible.risingempire.game.core.star.Star.Prototype;
import com.scheible.risingempire.game.core.star.Stars;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 *
 * @author sj
 */
public class Universe {
	
	private final static List<Prototype> STAR_PROTOTYPES = Arrays.asList(new Prototype("Tesh", 100,80),
			new Prototype("Dolz", 280,100),
			new Prototype("Alaozar", 460,60),
			new Prototype("Nagar", 640,40),
			new Prototype("Praecipua", 200, 200),
			new Prototype("Kled", 540, 180),
			new Prototype("Balin", 380, 240),
			new Prototype("Trigon", 700, 200),
			new Prototype("Uxnai", 100, 340),
			new Prototype("Draconis", 260, 340),
			new Prototype("Sagan", 580, 300),
			new Prototype("Hut", 440, 380),
			new Prototype("Burin", 160, 480),
			new Prototype("Proctor", 320, 460),
			new Prototype("Pesci", 720, 400),
			new Prototype("Seki", 500, 480),
			new Prototype("Tycho", 660, 520));		
	
	private int fleedId = 100;
    private int turn = 0;
    
    private final List<Leader> leaders = new ArrayList<>();	
	private final Map<Leader, Ai> aiLeaders = new HashMap<>();	
	
	private final Stars stars = new Stars();	
	private final Fleets fleets = new Fleets();
	
    private final Map<Leader, List<Command>> leaderCommands = new HashMap<>();
	
	private final List<Event> events = new ArrayList<>();

	public Universe() {
		for(Prototype prototype : STAR_PROTOTYPES) {
			stars.add(new Star(prototype.getName(), prototype.getLocation(), Optional.empty()));
		}
	}

	public void register(Leader leader) {
		register(leader, false);
	}
	
	public void addAi(Leader leader) {
		register(leader, true);
	}	
	
    private void register(Leader leader, boolean isAi) {
		if(leaders.contains(leader)) {
			throw new IllegalStateException("Leader is already registered!");
		}
		
		Star removedStar = getRandomUncolonizedStar();
		stars.remove(removedStar);
		Star home = new Star(leader.getHomeStar(), removedStar.getLocation(), Optional.of(new Colony(leader, 40)));
		stars.add(home);
		fleets.add(new Fleet(fleedId++, leader, home));
        leaders.add(leader);
		
		if(isAi) {
			aiLeaders.put(leader, new FleetSendingAi());
		}
    }	
	
	private Star getRandomUncolonizedStar() {
		Random random = new Random();
		Star result = null;
		
		while(result == null) {
			Star star = stars.get(random.nextInt(stars.size()));
			if(!star.getColony().isPresent()) {
				result = star;
			}
		}
		
		return result;
	}	

	public List<Leader> getLeaders() {
		return leaders;
	}
	
	public boolean isAi(Leader leader) {
		return aiLeaders.containsKey(leader);
	}	
	
    public Map<Leader, Boolean> getTurnFinishInfo() {
        Map<Leader, Boolean> result = new HashMap<>();
        
        for(Leader leader : leaders) {
			if(!aiLeaders.keySet().contains(leader)) {
				result.put(leader, leaderCommands.containsKey(leader));
			} else {
				result.put(leader, true);
			}
        }
        
        return result;
    }
    
    public boolean process(Leader leader, List<Command> commands) {
        leaderCommands.put(leader, commands);
		
		Map<Leader, View> views = getViews();
        
		boolean turnFinished = leaderCommands.size() == leaders.size() - aiLeaders.size();
        if(turnFinished) {
			events.clear();
			
			for(Leader aiLeader : aiLeaders.keySet()) {
				leaderCommands.put(aiLeader, aiLeaders.get(aiLeader).think(aiLeader, views.get(aiLeader)));
			}
			
			for (Leader currentLeader : leaderCommands.keySet()) {
				for (Fleet fleet : fleets) {
					fleet.turn(stars, leaderCommands.get(currentLeader));
				}
			}
			
            leaderCommands.clear();
            turn++;
			
			// NOTE Dummy event for now... but should cheer you up! ;-)
			if(turn % 7 == 0) {
				for(Leader currentLeader : leaders) {
					events.add(new RandomMessageEvent(currentLeader, "Your empire is the best since " + turn + " turns!"));
				}
			}
			
            return true;
        }
        
        return false;
    }
	
	public Map<Leader, View> getViews() {
		return ViewGenerator.create(leaders, stars, fleets, events);
	}
	
    public int getTurn() {
        return turn;
    }	
}
