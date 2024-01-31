package com.scheible.risingempire.game.impl.fleet;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.impl.system.SystemOrb;

/**
 * @author sj
 */
public class FleetFinder {

	private final Map<FleetId, Fleet> fleets;

	private final JourneyCalculator journeyCalculator;

	public FleetFinder(Map<FleetId, Fleet> fleets, JourneyCalculator journeyCalculator) {
		this.fleets = Collections.unmodifiableMap(fleets);
		this.journeyCalculator = journeyCalculator;
	}

	public Optional<OrbitingFleet> getOrbitingFleet(Player player, SystemOrb system) {
		return getOrbitingFleet(system).stream().filter(of -> of.getPlayer() == player).findAny();
	}

	public Set<OrbitingFleet> getOrbitingFleet(SystemOrb system) {
		return this.fleets.values()
			.stream()
			.filter(f -> f instanceof OrbitingFleet)
			.map(f -> (OrbitingFleet) f)
			.filter(of -> of.getSystem().equals(system))
			.collect(Collectors.toSet());
	}

	public Optional<DeployedFleet> getJustLeavingFleets(Player player, SystemOrb source, SystemOrb destination,
			int speed) {
		return this.fleets.values()
			.stream()
			.filter(f -> f instanceof DeployedFleet)
			.map(f -> (DeployedFleet) f)
			.filter(df -> df.getPlayer() == player && df.getSource().equals(source)
					&& df.getDestination().equals(destination) && df.isJustLeaving())
			.filter(df -> this.journeyCalculator.calcFleetSpeed(player, df.getShips()) == speed)
			.findFirst();
	}

}
