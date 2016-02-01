package com.scheible.risingempire.game.core.fleet;

import java.util.ArrayList;

/**
 *
 * @author sj
 */
public class Fleets extends ArrayList<Fleet> {
	
	public Fleet getByFleetId(int fleetId) {
		for(Fleet fleet : this) {
			if(fleet.getId() == fleetId) {
				return fleet;
			}
		}
		
		throw new IllegalStateException("The flet with id = " + fleetId + " is unknown!");
	}
}
