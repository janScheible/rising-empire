package com.scheible.risingempire.game.impl2.intelligence;

import java.util.Optional;

import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public class Intelligence {

	public void recon() {
	}

	public boolean justExplored(Position position) {
		return false;
	}

	public SystemReconReport systemReconReport(Position system) {
		return new SystemReconReport(true, Optional.empty());
	}

	public FleetReconReport fleetReconReport(Position system) {
		return new FleetReconReport(true, Optional.empty());
	}

}
