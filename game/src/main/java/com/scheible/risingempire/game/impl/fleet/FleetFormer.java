package com.scheible.risingempire.game.impl.fleet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.system.SystemOrb;
import com.scheible.risingempire.util.ProcessingResult;

/**
 * @author sj
 */
public class FleetFormer {

	private final FleetIdGenerator fleetIdGenerator;

	private final FleetFinder fleetFinder;

	private final JourneyCalculator journeyCalculator;

	public FleetFormer(FleetIdGenerator fleetIdGenerator, FleetFinder fleetFinder,
			JourneyCalculator journeyCalculator) {
		this.fleetIdGenerator = fleetIdGenerator;
		this.fleetFinder = fleetFinder;
		this.journeyCalculator = journeyCalculator;
	}

	public FleetChanges deployFleet(Player player, Fleet from, SystemOrb source, SystemOrb destination,
			Map<DesignSlot, Integer> ships, int round) {
		int speed = this.journeyCalculator.calcFleetSpeed(player, ships);

		boolean isJustLeaveSentBack = from.isDeployed() && from.asDeployed().isJustLeaving()
				&& from.asDeployed().getSource().equals(destination);

		ProcessingResult<? extends Fleet> to = isJustLeaveSentBack
				? this.fleetFinder.getOrbitingFleet(player, source)
					.map(ProcessingResult::existing)
					.orElseGet(() -> ProcessingResult.created(new OrbitingFleet(this.fleetIdGenerator.createRandom(),
							player, new HashMap<>(), source, round)))
				: this.fleetFinder.getJustLeavingFleets(player, source, destination, speed)
					.map(ProcessingResult::existing)
					.orElseGet(() -> ProcessingResult.created(new DeployedFleet(this.fleetIdGenerator.createRandom(),
							player, new HashMap<>(), source, destination, speed)));

		from.detach(ships);
		updateSpeed(player, from);

		to.get().join(ships);
		updateSpeed(player, to.get());

		return new FleetChanges(to.wasCreated() ? Set.of(to.get()) : Set.of(),
				!from.hasShips() ? Set.of(from) : Set.of());
	}

	private void updateSpeed(Player player, Fleet fleet) {
		if (fleet.isDeployed() && fleet.hasShips()) {
			int newSpeed = this.journeyCalculator.calcFleetSpeed(player, fleet.getShips());

			if (fleet.asDeployed().getSpeed() != newSpeed) {
				fleet.asDeployed().setSpeed(newSpeed);
			}
		}
	}

	public ProcessingResult<OrbitingFleet> welcomeFleet(DeployedFleet fleet, SystemOrb destination, int round) {
		ProcessingResult<OrbitingFleet> orbiting = this.fleetFinder.getOrbitingFleet(fleet.getPlayer(), destination)
			.map(ProcessingResult::existing)
			.orElseGet(() -> ProcessingResult.created(new OrbitingFleet(this.fleetIdGenerator.createRandom(),
					fleet.getPlayer(), new HashMap<>(), destination, round)));
		orbiting.get().join(fleet.getShips());
		return orbiting;
	}

}
