package com.scheible.risingempire.game.core;

import com.scheible.risingempire.game.common.Player;
import com.scheible.risingempire.game.common.event.Event;
import com.scheible.risingempire.game.common.event.RandomMessageEvent;
import com.scheible.risingempire.game.common.view.View;
import com.scheible.risingempire.game.common.view.fleet.Fleet;
import com.scheible.risingempire.game.common.view.star.Star;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sj
 */
public class ViewGenerator {

	public static Map<Leader, View> create(List<Leader> leaders, List<com.scheible.risingempire.game.core.star.Star> stars,
			List<com.scheible.risingempire.game.core.fleet.Fleet> fleets, List<com.scheible.risingempire.game.core.event.Event> events) {
		Map<Leader, View> views = new HashMap<>();

		for (Leader leader : leaders) {
			List<Star> viewStars = new ArrayList<>();
			for (com.scheible.risingempire.game.core.star.Star star : stars) {
				if (!star.getColony().isPresent()) {
					viewStars.add(Star.createAnonymousStar(star.getName(), (int) star.getLocation().getX(), (int) star.getLocation().getY()));
				} else if (star.getColony().get().getLeader().equals(leader)) {
					viewStars.add(Star.createOwnedStar(star.getName(), (int) star.getLocation().getX(), (int) star.getLocation().getY(),
							star.getColony().get().getLeader().getNation(), star.getColony().get().getPopulation()));
				} else {
					viewStars.add(Star.createAlienStar(star.getName(), (int) star.getLocation().getX(), (int) star.getLocation().getY(),
							star.getColony().get().getLeader().getNation()));
				}
			}

			List<com.scheible.risingempire.game.common.view.fleet.Fleet> viewFleets = new ArrayList<>();
			for (com.scheible.risingempire.game.core.fleet.Fleet fleet : fleets) {
				if (fleet.isOrbiting()) {
					viewFleets.add(Fleet.createOrbitingFleet(fleet.getId(), fleet.getStar().getName(), fleet.getLeader().getNation(), fleet.getLeader().equals(leader)));
				} else {
					viewFleets.add(Fleet.createTravelingFleet(fleet.getId(), (int)fleet.getLocation().getX(), (int)fleet.getLocation().getY(), fleet.getLeader().getNation(), false));
				}
			}

			List<Player> players = new ArrayList<>();
			for (Leader nationLeader : leaders) {
				players.add(new Player(nationLeader.getName(), nationLeader.getNation()));
			}
			
			List<Event> leaderEvents = new ArrayList<>();
			for(com.scheible.risingempire.game.core.event.Event event : events) {
				if(event.getLeader().equals(leader)) {
					if(event instanceof com.scheible.risingempire.game.core.event.RandomMessageEvent) {
						com.scheible.risingempire.game.core.event.RandomMessageEvent randomMessageEvent = (com.scheible.risingempire.game.core.event.RandomMessageEvent) event;
						leaderEvents.add(new RandomMessageEvent(randomMessageEvent.getMessage()));
					} else {
						throw new IllegalStateException("Can't translate event of type '" + event.getClass().getSimpleName() + "' to a view tpye!");
					}
				}
			}

			views.put(leader, new View(leader.getNation(), players, viewStars, viewFleets, leaderEvents));
		}

		return views;
	}
}
