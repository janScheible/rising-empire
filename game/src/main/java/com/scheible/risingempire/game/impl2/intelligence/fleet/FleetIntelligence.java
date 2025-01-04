package com.scheible.risingempire.game.impl2.intelligence.fleet;

import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.intelligence.fleet.FleetItinearySegmentProvider.FleetItinerarySegment;
import com.scheible.risingempire.game.impl2.intelligence.fleet.ScanAreasProvider.ScanArea;
import com.scheible.risingempire.game.impl2.util.Line;

/**
 * @author sj
 */
public class FleetIntelligence {

	private final ScanAreasProvider scanAreasProvider;

	private final ShipScannerSpecsProvider shipScannerSpecsProvider;

	private final FleetItinearySegmentProvider fleetItinearySegmentProvider;

	public FleetIntelligence(ScanAreasProvider scanAreasProvider, ShipScannerSpecsProvider shipScannerSpecsProvider,
			FleetItinearySegmentProvider fleetItinearySegmentProvider) {
		this.scanAreasProvider = scanAreasProvider;
		this.shipScannerSpecsProvider = shipScannerSpecsProvider;
		this.fleetItinearySegmentProvider = fleetItinearySegmentProvider;
	}

	public FleetReconReport fleetReconReport(Player player, Position fleet) {
		Optional<FleetItinerarySegment> itinerarySegment = this.fleetItinearySegmentProvider
			.fleetItinerarySegment(player, fleet);

		// for now every scanned fleet is visible in the whole itinerary segment - a
		// possible impovement is to scrop the segment to only the visible part
		boolean scanned = false;
		for (ScanArea scanArea : this.scanAreasProvider.scanAreas(player)) {
			if (intersects(itinerarySegment.map(FleetItinerarySegment::origin).orElse(fleet),
					itinerarySegment.map(FleetItinerarySegment::destination).orElse(fleet), scanArea)) {
				scanned = true;
				break;
			}
		}

		return new FleetReconReport(scanned,
				scanned && this.shipScannerSpecsProvider.shipScannerRevealesItineary(player));
	}

	private boolean intersects(Position origin, Position destination, ScanArea scanArea) {
		if (origin.equals(destination)) {
			return !origin.subtract(scanArea.position()).length().greaterThan(scanArea.radius());
		}
		else {
			// axis-aligned bounding box for the itinerary segment
			long originX = origin.x().toMilliparsec();
			long originY = origin.y().toMilliparsec();
			long destinationX = destination.x().toMilliparsec();
			long destinationY = destination.y().toMilliparsec();

			long segmentBoxLeft = Math.min(originX, destinationX);
			long segmentBoxTop = Math.min(originY, destinationY);
			long segmentBoxRight = Math.max(originX, destinationX);
			long segmentBoxBottom = Math.max(originY, destinationY);

			// axis-aligned bounding box for the scan area circle
			long scanAreaX = scanArea.position().x().toMilliparsec();
			long scanAreaY = scanArea.position().y().toMilliparsec();
			long scanAreaRadius = scanArea.radius().toMilliparsec();

			long areaBoxLeft = scanAreaX - scanAreaRadius;
			long areaBoxTop = scanAreaY - scanAreaRadius;
			long areaBoxRight = scanAreaX + scanAreaRadius;
			long areaBoxBottom = scanAreaY + scanAreaRadius;

			// cheap axis-aligned bounding box intersection check
			boolean intersects = segmentBoxLeft < areaBoxRight && segmentBoxRight > areaBoxLeft
					&& segmentBoxTop < areaBoxBottom && segmentBoxBottom > areaBoxTop;
			if (intersects) {
				// a more expensive real intersection check as a line instead of segment,
				// that means tangents are counted as not intersected
				return new Line(originX, originY, destinationX, destinationY)
					.intersectCircle(scanAreaX, scanAreaY, scanAreaRadius)
					.size() == 2;
			}
		}

		return false;
	}

}
