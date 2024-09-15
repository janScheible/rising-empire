package com.scheible.risingempire.game.impl.fleet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrivalBuilder;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.impl.ship.ShipDesignProvider;
import com.scheible.risingempire.game.impl.spacecombat.SpaceCombat;
import com.scheible.risingempire.game.impl.system.System;
import com.scheible.risingempire.game.impl.system.SystemOrb;
import com.scheible.risingempire.game.impl.system.SystemSnapshot;
import com.scheible.risingempire.game.impl.system.SystemSnapshotter;
import com.scheible.risingempire.util.ProcessingResult;

/**
 * @author sj
 */
public class FleetTurn {

	private final Supplier<Integer> roundProvider;

	private final Map<SystemId, System> systems;

	private final SystemSnapshotter snapshotter;

	private final FleetFormer fleetFormer;

	private final FleetFinder fleetFinder;

	private final SpaceCombatResolver spaceCombatResolver;

	private final ShipDesignProvider shipDesignProvider;

	public FleetTurn(Supplier<Integer> roundProvider, Map<SystemId, System> systems, SystemSnapshotter snapshotter,
			FleetFormer fleetFormer, FleetFinder fleetFinder, SpaceCombatResolver spaceCombatResolver,
			ShipDesignProvider shipDesignProvider) {
		this.roundProvider = roundProvider;
		this.systems = systems;
		this.snapshotter = snapshotter;
		this.fleetFormer = fleetFormer;
		this.fleetFinder = fleetFinder;
		this.spaceCombatResolver = spaceCombatResolver;
		this.shipDesignProvider = shipDesignProvider;
	}

	public FleetChanges nextTurn(Fleet fleet) {
		Set<Fleet> emptyFleets = new HashSet<>();
		Set<Fleet> newFleets = new HashSet<>();
		List<SpaceCombat> combats = new ArrayList<>();

		// keep track of the original fleet ids that arrived and will be merged to a
		// orbiting fleet
		Map<FleetId, Set<FleetBeforeArrival>> orbitingArrivingMapping = new HashMap<>();

		if (fleet.isDeployed()) {
			DeployedFleet deployedFleet = fleet.asDeployed();
			deployedFleet.turn();

			if (deployedFleet.hasArrived()) {
				SystemOrb destination = deployedFleet.getDestination();

				ProcessingResult<OrbitingFleet> orbitingFleet = this.fleetFormer.welcomeFleet(deployedFleet,
						destination, this.roundProvider.get());
				if (orbitingFleet.wasCreated()) {
					newFleets.add(orbitingFleet.get());
				}
				emptyFleets.add(deployedFleet);
				orbitingArrivingMapping.computeIfAbsent(orbitingFleet.get().getId(), key -> new HashSet<>())
					.add(FleetBeforeArrivalBuilder.builder()
						.id(deployedFleet.getId())
						.horizontalDirection(deployedFleet.getHorizontalDirection())
						.speed(deployedFleet.getSpeed())
						.location(deployedFleet.getPreviousLocation())
						.justLeaving(deployedFleet.isPreviousJustLeaving())
						.build());

				System destinationSystem = this.systems.get(destination.getId());
				if (!destinationSystem.getColony(fleet.getPlayer()).isPresent()) {
					this.snapshotter.put(fleet.getPlayer(), destination.getId(),
							SystemSnapshot.forKnown(this.roundProvider.get(), destinationSystem));
				}

				this.fleetFinder.getOrbitingFleet(destinationSystem)
					.stream()
					.filter(clashingOrbitingFleet -> clashingOrbitingFleet.getPlayer() != deployedFleet.getPlayer())
					.forEach(defendingFleet -> {
						// TODO This is not correct... space combats must be done after
						// all
						// fleets had the chance to arrive (SpaceCombatResolver needs a
						// possibility to be passed all the arriving fleets)
						SpaceCombat spaceCombat = this.spaceCombatResolver.resolve(destinationSystem.getId(),
								defendingFleet, deployedFleet, this.shipDesignProvider);
						if (spaceCombat.getOutcome() == Outcome.ATTACKER_WON) {
							emptyFleets.add(defendingFleet);
							orbitingFleet.get().retain(spaceCombat.getAttackerShipCounts());
						}
						else if (spaceCombat.getOutcome() == Outcome.DEFENDER_WON) {
							newFleets.clear();
							defendingFleet.retain(spaceCombat.getDefenderShipCounts());
						}
						else { // ATTACKER_RETREATED
							defendingFleet.retain(spaceCombat.getDefenderShipCounts());

							newFleets.clear();
							newFleets.addAll(
									this.fleetFormer
										.deployFleet(deployedFleet.getPlayer(), deployedFleet,
												deployedFleet.getDestination(), deployedFleet.getSource(),
												spaceCombat.getAttackerShipCounts(), this.roundProvider.get())
										.getAdded());
							assertSingleFleet(newFleets);

							FleetId newReturningFleetId = newFleets.iterator().next().getId();
							orbitingArrivingMapping.put(newReturningFleetId,
									orbitingArrivingMapping.remove(orbitingFleet.get().getId()));
						}

						combats.add(spaceCombat);
					});
			}
		}
		else if (fleet.isOrbiting()) {
			OrbitingFleet orbitingFleet = fleet.asOrbiting();
			System orbitingSystem = this.systems.get(orbitingFleet.getSystem().getId());

			if (!orbitingSystem.getColony(fleet.getPlayer()).isPresent()) {
				this.snapshotter.put(fleet.getPlayer(), orbitingFleet.getSystem().getId(),
						SystemSnapshot.forKnown(this.roundProvider.get(), orbitingSystem));
			}
		}

		if (combats.size() > 1) {
			throw new IllegalStateException(
					"At max only a single fleet should be in orbit and therefore only a single combat can occur!");
		}
		return new FleetChanges(newFleets, emptyFleets, combats, orbitingArrivingMapping);
	}

	private static void assertSingleFleet(Set<Fleet> newFleets) throws IllegalStateException {
		if (newFleets.size() != 1) {
			throw new IllegalStateException("There should only be one leaving fleet!");
		}
	}

}
