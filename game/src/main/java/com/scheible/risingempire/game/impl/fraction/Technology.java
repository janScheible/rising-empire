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

	public Technology(double fleetRangeFactor) {
		this.fleetRangeFactor = fleetRangeFactor;
	}

	public int getFleetRange() {
		return (int) (this.fleetRange * this.fleetRangeFactor);
	}

	public int getExtendedFleetRange() {
		return (int) (this.fleetRange * this.fleetRangeFactor * 1.3);
	}

	public int getColonyScannerRange() {
		return this.colonyScannerRange;
	}

	public int getFleetScannerRange() {
		return this.fleetScannerRange;
	}

	@Override
	public String toString() {
		return toStringBuilder(getClass()).add("fleetRange", this.fleetRange)
			.add("colonyScannerRange", this.colonyScannerRange)
			.add("fleetScannerRange", this.fleetScannerRange)
			.add("fleetRangeFactor", this.fleetRangeFactor)
			.toString();
	}

}
