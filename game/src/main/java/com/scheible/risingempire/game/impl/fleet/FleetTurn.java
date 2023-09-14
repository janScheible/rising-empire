package com.scheible.risingempire.game.impl.fleet;

import static com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome.ATTACKER_WON;
import static com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome.DEFENDER_WON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.impl.ship.ShipDesignProvider;
import com.scheible.risingempire.game.impl.spacecombat.SpaceCombat;
import com.scheible.risingempire.game.impl.system.System;
import com.scheible.risingempire.game.impl.system.SystemOrb;
import com.scheible.risingempire.game.impl.system.SystemSnapshot;
import com.scheible.risingempire.game.impl.system.SystemSnapshotter;
import com.scheible.risingempire.util.ProcessingResult;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
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

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2")
	public FleetTurn(final Supplier<Integer> roundProvider, final Map<SystemId, System> systems,
			final SystemSnapshotter snapshotter, final FleetFormer fleetFormer, final FleetFinder fleetFinder,
			final SpaceCombatResolver spaceCombatResolver, final ShipDesignProvider shipDesignProvider) {
		this.roundProvider = roundProvider;
		this.systems = systems;
		this.snapshotter = snapshotter;
		this.fleetFormer = fleetFormer;
		this.fleetFinder = fleetFinder;
		this.spaceCombatResolver = spaceCombatResolver;
		this.shipDesignProvider = shipDesignProvider;
	}

	public FleetChanges nextTurn(final Fleet fleet) {
		final Set<Fleet> emptyFleets = new HashSet<>();
		final Set<Fleet> newFleets = new HashSet<>();
		final List<SpaceCombat> combats = new ArrayList<>();

		// keep track of the original fleet ids that arrived and will be merged to a orbiting fleet
		final Map<FleetId, Set<FleetBeforeArrival>> orbitingArrivingMapping = new HashMap<>();

		if (fleet.isDeployed()) {
			final DeployedFleet deployedFleet = fleet.asDeployed();
			deployedFleet.turn();

			if (deployedFleet.hasArrived()) {
				final SystemOrb destination = deployedFleet.getDestination();

				final ProcessingResult<OrbitingFleet> orbitingFleet = fleetFormer.welcomeFleet(deployedFleet,
						destination, roundProvider.get());
				if (orbitingFleet.wasCreated()) {
					newFleets.add(orbitingFleet.get());
				}
				emptyFleets.add(deployedFleet);
				orbitingArrivingMapping.computeIfAbsent(orbitingFleet.get().getId(), key -> new HashSet<>())
						.add(new FleetBeforeArrival(deployedFleet.getId(), deployedFleet.getHorizontalDirection(),
								deployedFleet.getSpeed()));

				final System destinationSystem = systems.get(destination.getId());
				if (!destinationSystem.getColony(fleet.getPlayer()).isPresent()) {
					snapshotter.put(fleet.getPlayer(), destination.getId(),
							SystemSnapshot.forKnown(roundProvider.get(), destinationSystem));
				}

				fleetFinder.getOrbitingFleet(destinationSystem).stream()
						.filter(clashingOrbitingFleet -> clashingOrbitingFleet.getPlayer() != deployedFleet.getPlayer())
						.forEach(defendingFleet -> {
							final SpaceCombat spaceCombat = spaceCombatResolver.resolve(destinationSystem.getId(),
									defendingFleet, deployedFleet, shipDesignProvider);
							if (spaceCombat.getOutcome() == ATTACKER_WON) {
								emptyFleets.add(defendingFleet);
							} else if (spaceCombat.getOutcome() == DEFENDER_WON) {
								newFleets.clear();
							} else { // ATTACKER_RETREATED
								defendingFleet.retain(spaceCombat.getDefenderShipCounts());

								newFleets.clear();
								newFleets
										.addAll(fleetFormer
												.deployFleet(deployedFleet.getPlayer(), deployedFleet,
														deployedFleet.getDestination(), deployedFleet.getSource(),
														spaceCombat.getAttackerShipCounts(), roundProvider.get())
												.getAdded());
								assertSingleFleet(newFleets);

								final FleetId newReturningFleetId = newFleets.iterator().next().getId();
								orbitingArrivingMapping.put(newReturningFleetId,
										orbitingArrivingMapping.remove(orbitingFleet.get().getId()));
							}

							combats.add(spaceCombat);
						});
			}
		} else if (fleet.isOrbiting()) {
			final OrbitingFleet orbitingFleet = fleet.asOrbiting();
			final System orbitingSystem = systems.get(orbitingFleet.getSystem().getId());

			if (!orbitingSystem.getColony(fleet.getPlayer()).isPresent()) {
				snapshotter.put(fleet.getPlayer(), orbitingFleet.getSystem().getId(),
						SystemSnapshot.forKnown(roundProvider.get(), orbitingSystem));
			}
		}

		if (combats.size() > 1) {
			throw new IllegalStateException(
					"At max only a single fleet should be in orbit and therefore only a single combat can occur!");
		}
		return new FleetChanges(newFleets, emptyFleets, combats, orbitingArrivingMapping);
	}

	@SuppressFBWarnings(value = "DRE_DECLARED_RUNTIME_EXCEPTION", justification = "Should never happen, is like an assert.")
	private static void assertSingleFleet(final Set<Fleet> newFleets) throws IllegalStateException {
		if (newFleets.size() != 1) {
			throw new IllegalStateException("There should only be one leaving fleet!");
		}
	}
}
