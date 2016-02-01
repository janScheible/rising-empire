package com.scheible.risingempire.game.ai;

import com.google.common.collect.Lists;
import com.scheible.risingempire.game.common.command.Command;
import com.scheible.risingempire.game.common.command.FleetDispatchCommand;
import com.scheible.risingempire.game.common.view.View;
import com.scheible.risingempire.game.common.view.fleet.Fleet;
import com.scheible.risingempire.game.common.view.star.Star;
import com.scheible.risingempire.game.core.Leader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author sj
 */
public class FleetSendingAi implements Ai {

	private int waitCounter = 0;
	
	@Override
	public List<Command> think(Leader leader, View view) {
		Fleet ownFleet = null;
		for(Fleet fleet : view.getFleets()) {
			if(fleet.getNation().equals(leader.getNation())) {
				ownFleet = fleet;
				break;
			}
		}
		
		if(ownFleet != null && ownFleet.isDispatchable()) {			
			if(waitCounter == 0) {
				waitCounter = new Random().nextInt(6);
				Star destinationStar = null;
				
				while(destinationStar == null) {
					Star destinationStarCandidate = view.getStars().get(new Random().nextInt(view.getStars().size()));
					if(!destinationStarCandidate.getName().equals(ownFleet.getStar())) {
						destinationStar = destinationStarCandidate;
					}
				}
				
				return Lists.newArrayList(new FleetDispatchCommand(ownFleet.getId(), destinationStar.getName()));
			} else {
				waitCounter--;				
			}
		}
		
		return new ArrayList<>();
	}
}
