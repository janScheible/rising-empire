package com.scheible.risingempire.game.impl.fleet;

import java.util.HashMap;
import java.util.Map;

import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.system.SystemOrb;
import com.scheible.risingempire.util.ProcessingResult;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

/**
 * @author sj
 */
public class FleetFormer {

	private final FleetIdGenerator fleetIdGenerator;

	private final FleetFinder fleetFinder;

	private final JourneyCalculator journeyCalculator;

	public FleetFormer(final FleetIdGenerator fleetIdGenerator, final FleetFinder fleetFinder,
			final JourneyCalculator journeyCalculator) {
		this.fleetIdGenerator = fleetIdGenerator;
		this.fleetFinder = fleetFinder;
		this.journeyCalculator = journeyCalculator;
	}

	public FleetChanges deployFleet(final Player player, final Fleet from, final SystemOrb source,
			final SystemOrb destination, final Map<DesignSlot, Integer> ships, final int round) {
		final int speed = journeyCalculator.calcFleetSpeed(player, ships);

		final boolean isJustLeaveSentBack = from.isDeployed() && from.asDeployed().isJustLeaving()
				&& from.asDeployed().getSource().equals(destination);

		final ProcessingResult<? extends Fleet> to = isJustLeaveSentBack
				? fleetFinder.getOrbitingFleet(player, source)
					.map(ProcessingResult::existing)
					.orElseGet(() -> ProcessingResult.created(
							new OrbitingFleet(fleetIdGenerator.createRandom(), player, new HashMap<>(), source, round)))
				: fleetFinder.getJustLeavingFleets(player, source, destination, speed)
					.map(ProcessingResult::existing)
					.orElseGet(() -> ProcessingResult.created(new DeployedFleet(fleetIdGenerator.createRandom(), player,
							new HashMap<>(), source, destination, speed)));

		from.detach(ships);
		updateSpeed(player, from);

		to.get().join(ships);
		updateSpeed(player, to.get());

		return new FleetChanges(to.wasCreated() ? singleton(to.get()) : emptySet(),
				!from.hasShips() ? singleton(from) : emptySet());
	}

	private void updateSpeed(final Player player, final Fleet fleet) {
		if (fleet.isDeployed() && fleet.hasShips()) {
			final int newSpeed = journeyCalculator.calcFleetSpeed(player, fleet.getShips());

			if (fleet.asDeployed().getSpeed() != newSpeed) {
				fleet.asDeployed().setSpeed(newSpeed);
			}
		}
	}

	public ProcessingResult<OrbitingFleet> welcomeFleet(final DeployedFleet fleet, final SystemOrb destination,
			final int round) {
		final ProcessingResult<OrbitingFleet> orbiting = fleetFinder.getOrbitingFleet(fleet.getPlayer(), destination)
			.map(ProcessingResult::existing)
			.orElseGet(() -> ProcessingResult.created(new OrbitingFleet(fleetIdGenerator.createRandom(),
					fleet.getPlayer(), new HashMap<>(), destination, round)));
		orbiting.get().join(fleet.getShips());
		return orbiting;
	}

}
