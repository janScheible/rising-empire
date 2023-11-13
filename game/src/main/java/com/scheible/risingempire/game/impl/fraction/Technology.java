package com.scheible.risingempire.game.impl.fraction;

import static com.scheible.risingempire.util.jdk.Objects2.toStringBuilder;

/**
 * @author sj
 */
public class Technology {

	private int fleetRange = 200;

	private int colonyScannerRange = 80;

	private int fleetScannerRange = 30;

	private final double fleetRangeFactor;

	public Technology(final double fleetRangeFactor) {
		this.fleetRangeFactor = fleetRangeFactor;
	}

	public int getFleetRange() {
		return (int) (fleetRange * fleetRangeFactor);
	}

	public int getExtendedFleetRange() {
		return (int) (fleetRange * fleetRangeFactor * 1.3);
	}

	public int getColonyScannerRange() {
		return colonyScannerRange;
	}

	public int getFleetScannerRange() {
		return fleetScannerRange;
	}

	@Override
	public String toString() {
		return toStringBuilder(getClass()).add("fleetRange", fleetRange)
			.add("colonyScannerRange", colonyScannerRange)
			.add("fleetScannerRange", fleetScannerRange)
			.add("fleetRangeFactor", fleetRangeFactor)
			.toString();
	}

}
