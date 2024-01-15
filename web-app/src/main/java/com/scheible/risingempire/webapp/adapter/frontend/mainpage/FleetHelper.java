package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.MainPage.StarMapFleet;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.MainPage.StarMapFleetBeforeArrival;

/**
 * @author sj
 */
class FleetHelper {

	private FleetHelper() {
	}

	static Stream<StarMapFleet> toStarMapFleets(FleetView fleet, Set<SpaceCombatView> spaceCombats,
			MainPageState state) {
		// for arriving space combat fleets we want them to stay in the middle of the star
		// to not cover the attacked
		// fleet
		boolean arrivingSpaceCombatFleet = spaceCombats.stream()
			.filter(sc -> state.isSpaceCombatSystemState()
					&& sc.getOrder() >= state.asSpaceCombatSystemState().getOrder())
			.filter(sc -> fleet.getFleetsBeforeArrival().stream().anyMatch(fba -> sc.getAttackerFleets().contains(fba)))
			.findFirst()
			.isPresent();

		boolean animateArriving = state.isFleetMovementState() && fleet.didJustArrive()
				|| state.isSpaceCombatSystemState() && arrivingSpaceCombatFleet;

		// for animated fleets in the last turn before arriving in a system we need to get
		// back the original deployed
		// fleets with their ids
		return (animateArriving
				? fleet.getFleetsBeforeArrival()
					.stream()
					.map(fiba -> new StarMapFleetBeforeArrival(fiba.getId(), Optional.of(fiba.getHorizontalDirection()),
							Optional.of(fiba.getSpeed())))
				: Stream
					.of(new StarMapFleetBeforeArrival(fleet.getId(), fleet.getHorizontalDirection(), fleet.getSpeed())))
			.map(fleetBeforeArrival -> {
				boolean orbiting = !animateArriving && fleet.getOrbiting().isPresent();
				boolean justLeaving = !animateArriving && fleet.isJustLeaving().orElse(Boolean.FALSE);

				return new StarMapFleet(fleetBeforeArrival.id, fleet.getPlayer(), fleet.getLocation().getX(),
						fleet.getLocation().getY(), orbiting, justLeaving, fleet.getDestination(),
						fleetBeforeArrival.speed, fleetBeforeArrival.horizontalDirection);
			});
	}

	/**
	 * In case of a lost space combat the own fleet was already removed (same it true for
	 * a defeated foreign fleet).
	 */
	static Stream<StarMapFleet> createSpaceCombatFakeFleets(GameView gameView, MainPageState state) {
		Set<FleetId> fleetIds = gameView.getFleets()
			.stream()
			.flatMap(fleet -> Stream.concat(Stream.of(fleet.getId()),
					fleet.getFleetsBeforeArrival().stream().map(FleetBeforeArrival::getId)))
			.collect(Collectors.toSet());

		return gameView.getSpaceCombats()
			.stream()
			.filter(sc -> state.isFleetMovementState()
					|| state.isSpaceCombatSystemState() && sc.getOrder() >= state.asSpaceCombatSystemState().getOrder())
			.flatMap(sc -> {
				Location spaceCombatLocation = gameView.getSystem(sc.getSystemId()).getLocation();
				List<StarMapFleet> starMapFleets = new ArrayList<>();

				for (FleetBeforeArrival arrivingAttackerFleet : sc.getAttackerFleets()) {
					if (!fleetIds.contains(arrivingAttackerFleet.getId())) {
						fleetIds.add(arrivingAttackerFleet.getId());

						starMapFleets.add(StarMapFleet.createArrivingSpaceCombatFakeFleet(arrivingAttackerFleet.getId(),
								sc.getAttackerPlayer(), sc.getSystemId(), arrivingAttackerFleet.getSpeed(),
								arrivingAttackerFleet.getHorizontalDirection(), spaceCombatLocation.getX(),
								spaceCombatLocation.getY()));
					}
				}

				if (sc.getDefenderFleet().isPresent()) {
					FleetId orbitingDefenderFleet = sc.getDefenderFleet().get();

					if (!fleetIds.contains(orbitingDefenderFleet)) {
						fleetIds.add(orbitingDefenderFleet);

						starMapFleets.add(StarMapFleet.createOrbitingSpaceCombatFakeFleet(orbitingDefenderFleet,
								sc.getDefenderPlayer(), spaceCombatLocation.getX(), spaceCombatLocation.getY()));
					}
				}

				for (FleetBeforeArrival arrivingDefenderFleet : sc.getDefenderFleetsBeforeArrival()) {
					if (!fleetIds.contains(arrivingDefenderFleet.getId())) {
						fleetIds.add(arrivingDefenderFleet.getId());

						starMapFleets.add(StarMapFleet.createArrivingSpaceCombatFakeFleet(arrivingDefenderFleet.getId(),
								sc.getDefenderPlayer(), sc.getSystemId(), arrivingDefenderFleet.getSpeed(),
								arrivingDefenderFleet.getHorizontalDirection(), spaceCombatLocation.getX(),
								spaceCombatLocation.getY()));
					}
				}

				return starMapFleets.stream();
			});
	}

}
