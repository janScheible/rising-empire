package com.scheible.risingempire.game.impl2.navy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Navy.Deploy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class DispatcherTest extends AbstractNavyTest {

	private final Round round = new Round(42);

	@Test
	void testDeployOrbiting() {
		List<Fleet> dispatched = dispatch(List.of(orbitingFleet(this.origin, ships(this.scout, 1))),
				List.of(deployOrbiting(this.origin, ships(this.scout, 1), this.destination)));

		assertThat(dispatched).containsOnly(justLeavingFleet(this.origin, this.destination, ships(this.scout, 1)));
	}

	@Test
	void testDeployOrbitingWithRemainingFleet() {
		List<Fleet> dispatched = dispatch(List.of(orbitingFleet(this.origin, ships(this.scout, 2))),
				List.of(deployOrbiting(this.origin, ships(this.scout, 1), this.destination)));

		assertThat(dispatched).containsOnly(justLeavingFleet(this.origin, this.destination, ships(this.scout, 1)),
				orbitingFleet(this.origin, ships(this.scout, 1)));
	}

	@Test
	void testDeployOrbitingWithAlreadyDeployed() {
		List<Fleet> dispatched = dispatch(
				List.of(orbitingFleet(this.origin, ships(this.scout, 1)),
						justLeavingFleet(this.origin, this.destination, ships(this.scout, 1))),
				List.of(deployOrbiting(this.origin, ships(this.scout, 1), this.destination)));

		assertThat(dispatched).containsOnly(justLeavingFleet(this.origin, this.destination, ships(this.scout, 2)));
	}

	@Test
	void testDeployOrbitingWithAlreadyDeployedAndMultipleSpeeds() {
		List<Fleet> dispatched = dispatch(
				List.of(orbitingFleet(this.origin, ships(this.scout, 1)),
						justLeavingFleet(this.origin, this.destination, ships(this.scout, 1)),
						justLeavingFleet(this.origin, this.destination, ships(this.enterprise, 1))),
				List.of(deployOrbiting(this.origin, ships(this.scout, 1), this.destination)));

		assertThat(dispatched).containsOnly( //
				justLeavingFleet(this.origin, this.destination, ships(this.scout, 2)),
				justLeavingFleet(this.origin, this.destination, ships(this.enterprise, 1)));
	}

	@Test
	void testDeployJustLeavingToNewDestination() {
		List<Fleet> dispatched = dispatch(
				List.of(justLeavingFleet(this.origin, this.destination, ships(this.scout, 1))),
				List.of(deployJustLeaving(this.origin, this.destination,
						this.shipMovementSpecsProvider.effectiveSpeed(this.player, Set.of(this.scout)),
						ships(this.scout, 1), this.otherDestination)));

		assertThat(dispatched).containsOnly(justLeavingFleet(this.origin, this.otherDestination, ships(this.scout, 1)));
	}

	@Test
	void testDeployJustLeavingToNewDestinationWithRemainingFleet() {
		List<Fleet> dispatched = dispatch(
				List.of(justLeavingFleet(this.origin, this.destination, ships(this.scout, 2))),
				List.of(deployJustLeaving(this.origin, this.destination,
						this.shipMovementSpecsProvider.effectiveSpeed(this.player, Set.of(this.scout)),
						ships(this.scout, 1), this.otherDestination)));

		assertThat(dispatched).containsOnly(justLeavingFleet(this.origin, this.destination, ships(this.scout, 1)),
				justLeavingFleet(this.origin, this.otherDestination, ships(this.scout, 1)));
	}

	@Test
	void testDeployJustLeavingBackToOrigin() {
		List<Fleet> dispatched = dispatch(
				List.of(justLeavingFleet(this.origin, this.destination, ships(this.scout, 1))),
				List.of(deployJustLeaving(this.origin, this.destination,
						this.shipMovementSpecsProvider.effectiveSpeed(this.player, Set.of(this.scout)),
						ships(this.scout, 1), this.origin)));

		assertThat(dispatched).containsOnly(orbitingFleet(this.origin, ships(this.scout, 1)));
	}

	@Test
	void testDeployJustLeavingBackToOriginWithRemainingFleet() {
		List<Fleet> dispatched = dispatch(
				List.of(justLeavingFleet(this.origin, this.destination, ships(this.scout, 2))),
				List.of(deployJustLeaving(this.origin, this.destination,
						this.shipMovementSpecsProvider.effectiveSpeed(this.player, Set.of(this.scout)),
						ships(this.scout, 1), this.origin)));

		assertThat(dispatched).containsOnly(orbitingFleet(this.origin, ships(this.scout, 1)),
				justLeavingFleet(this.origin, this.destination, ships(this.scout, 1)));
	}

	@Test
	void testDeployJustLeavingBackToOriginWithAlreadyOrbiting() {
		List<Fleet> dispatched = dispatch(
				List.of(orbitingFleet(this.origin, ships(this.scout, 1)),
						justLeavingFleet(this.origin, this.destination, ships(this.enterprise, 1))),
				List.of(deployJustLeaving(this.origin, this.destination,
						this.shipMovementSpecsProvider.effectiveSpeed(this.player, Set.of(this.enterprise)),
						ships(this.enterprise, 1), this.origin)));

		assertThat(dispatched).containsOnly(orbitingFleet(this.origin, ships(this.scout, 1, this.enterprise, 1)));
	}

	@Test
	void testDeployTwoJustLeavingWithDifferentSpeeds() {
		List<Fleet> dispatched = dispatch(
				List.of(justLeavingFleet(this.origin, destination, ships(this.scout, 1)),
						justLeavingFleet(this.origin, destination, ships(this.enterprise, 1))),
				List.of(deployJustLeaving(this.origin, this.destination,
						this.shipMovementSpecsProvider.effectiveSpeed(this.player, Set.of(this.enterprise)),
						ships(this.enterprise, 1), this.otherDestination)));

		assertThat(dispatched).containsOnly(justLeavingFleet(this.origin, destination, ships(this.scout, 1)),
				justLeavingFleet(this.origin, this.otherDestination, ships(this.enterprise, 1)));
	}

	@Test
	void testTransferColonists() {
		List<Fleet> dispatched = dispatch(List.of(), List.of(transferColonists(this.origin, 42, this.destination)));

		assertThat(dispatched).containsOnly(justLeavingtransporterFleet(this.origin, this.destination, 42));
	}

	@Test
	void testTransferColonistsWithAlreadyDeployed() {
		List<Fleet> dispatched = dispatch(List.of(justLeavingtransporterFleet(this.origin, this.destination, 21)),
				List.of(transferColonists(this.origin, 21, this.destination)));

		assertThat(dispatched).containsOnly(justLeavingtransporterFleet(this.origin, this.destination, 42));
	}

	@Test
	void testTransferColonistsCancel() {
		List<Fleet> dispatched = dispatch(List.of(justLeavingtransporterFleet(this.origin, this.destination, 42)),
				List.of(transferColonists(this.origin, 0, this.destination)));

		assertThat(dispatched).isEmpty();
	}

	private Fleet justLeavingFleet(Position origin, Position destination, Ships ships) {
		return new Fleet(this.player, new Itinerary(origin, destination, this.round,
				this.shipMovementSpecsProvider.effectiveSpeed(this.player, ships.counts().keySet())), ships);
	}

	private Fleet justLeavingtransporterFleet(Position origin, Position destination, int transporterCount) {
		return new Fleet(this.player,
				new Itinerary(origin, destination, this.round, this.shipMovementSpecsProvider
					.effectiveSpeed(this.player, Set.of(ShipClassId.COLONISTS_TRANSPORTER))),
				Ships.transporters(transporterCount));
	}

	private List<Fleet> dispatch(List<Fleet> fleets, List<Deploy> deployments) {
		List<Fleet> fleetsCopy = new ArrayList<>(fleets);
		Dispatcher dispatcher = new Dispatcher(new Fleets(fleetsCopy, this.shipMovementSpecsProvider));
		dispatcher.dispatch(this.round, deployments);
		return fleetsCopy;
	}

}
