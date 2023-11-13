package com.scheible.risingempire.game.impl.fleet;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.system.SystemOrb;

/**
 * @author sj
 */
public class FleetFinder {

	private final Map<FleetId, Fleet> fleets;

	private final JourneyCalculator journeyCalculator;

	public FleetFinder(final Map<FleetId, Fleet> fleets, final JourneyCalculator journeyCalculator) {
		this.fleets = Collections.unmodifiableMap(fleets);
		this.journeyCalculator = journeyCalculator;
	}

	public Optional<OrbitingFleet> getOrbitingFleet(final Player player, final SystemOrb system) {
		return getOrbitingFleet(system).stream().filter(of -> of.getPlayer() == player).findAny();
	}

	public Set<OrbitingFleet> getOrbitingFleet(final SystemOrb system) {
		return fleets.values()
			.stream()
			.filter(f -> f instanceof OrbitingFleet)
			.map(f -> (OrbitingFleet) f)
			.filter(of -> of.getSystem().equals(system))
			.collect(Collectors.toSet());
	}

	public Optional<DeployedFleet> getJustLeavingFleets(final Player player, final SystemOrb source,
			final SystemOrb destination, final int speed) {
		return fleets.values()
			.stream()
			.filter(f -> f instanceof DeployedFleet)
			.map(f -> (DeployedFleet) f)
			.filter(df -> df.getPlayer() == player && df.getSource().equals(source)
					&& df.getDestination().equals(destination) && df.isJustLeaving())
			.filter(df -> journeyCalculator.calcFleetSpeed(player, df.getShips()) == speed)
			.findFirst();
	}

}
