package com.scheible.risingempire.game.common.view;

import com.scheible.risingempire.game.common.Player;
import com.scheible.risingempire.game.common.event.Event;
import com.scheible.risingempire.game.common.view.fleet.Fleet;
import com.scheible.risingempire.game.common.view.star.Star;
import java.util.List;

/**
 *
 * @author sj
 */
public class View {
    
	private final List<Player> players;
	private final List<Star> stars;
	private final List<Fleet> fleets;
	
	private final List<Event> events;

	public View(String nation, List<Player> players, List<Star> stars, List<Fleet> fleets, List<Event> events) {
		this.players = players;
		this.stars = stars;
		this.fleets = fleets;
		
		this.events = events;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Star> getStars() {
		return stars;
	}

	public List<Fleet> getFleets() {
		return fleets;
	}

	public List<Event> getEvents() {
		return events;
	}
}
