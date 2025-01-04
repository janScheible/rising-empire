package com.scheible.risingempire.game.impl2.intelligence.fleet;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.intelligence.fleet.FleetItinearySegmentProvider.FleetItinerarySegment;
import com.scheible.risingempire.game.impl2.intelligence.fleet.ScanAreasProvider.ScanArea;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class FleetIntelligenceTest {

	@Test
	void testOrbitingFleetReconReport() {
		FleetIntelligence intelligence = new FleetIntelligence(
				(Player _) -> Set.of(new ScanArea(new Position(6.0, 6.0), new Parsec(5.0))), //
				(Player _) -> true, (Player _, Position _) -> Optional.empty());

		// clearly in the scan area
		assertThat(intelligence.fleetReconReport(Player.BLUE, new Position(6.0, 6.0)).scanned()).isTrue();
		// inside axis-aligned bounding box of scan area but outside of actual scan area
		assertThat(intelligence.fleetReconReport(Player.BLUE, new Position(1.0, 1.0)).scanned()).isFalse();
		// clearly outside of the scan area
		assertThat(intelligence.fleetReconReport(Player.BLUE, new Position(0.5, 0.5)).scanned()).isFalse();
	}

	@Test
	void testDeployedFleetReconReport() {
		Map<Position, FleetItinerarySegment> positionItineraryMapping = Stream.of( //
				// inside axis-aligned bounding box but outside of circle --> NOT scanned
				fleetItinerarySegment(new Position(1.5, 3.0), new Position(3.5, 1.5)), //
				// intersects circle and its axis-aligned bounding box --> scanned
				fleetItinerarySegment(new Position(7.0, 4.0), new Position(12.0, 8.0)), //
				// outside of axis-aligned bounding box of circle --> NOT scanned
				fleetItinerarySegment(new Position(0.0, 0.0), new Position(8.0, 0.5)))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		FleetIntelligence intelligence = new FleetIntelligence(
				(Player _) -> Set.of(new ScanArea(new Position(6.0, 6.0), new Parsec(5.0))), //
				(Player _) -> true,
				(Player _, Position position) -> Optional.ofNullable(positionItineraryMapping.get(position)));

		assertThat(intelligence.fleetReconReport(Player.BLUE, new Position(1.5, 3.0)).scanned()).isFalse();
		assertThat(intelligence.fleetReconReport(Player.BLUE, new Position(7.0, 4.0)).scanned()).isTrue();
		assertThat(intelligence.fleetReconReport(Player.BLUE, new Position(0.0, 0.0)).scanned()).isFalse();

	}

	private static Entry<Position, FleetItinerarySegment> fleetItinerarySegment(Position origin, Position destination) {
		return Map.entry(origin, new FleetItinerarySegment(origin, destination));
	}

}