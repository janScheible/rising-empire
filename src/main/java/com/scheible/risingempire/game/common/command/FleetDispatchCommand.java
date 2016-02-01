package com.scheible.risingempire.game.common.command;

/**
 * 
 * @author sj
 */
public class FleetDispatchCommand extends Command {
    
    private final int fleetId;
    private final String destinationStar;

	public FleetDispatchCommand(int fleetId, String destinationStar) {
		this.fleetId = fleetId;
		this.destinationStar = destinationStar;
	}

	public int getFleetId() {
		return fleetId;
	}

	public String getDestinationStar() {
		return destinationStar;
	}
}
