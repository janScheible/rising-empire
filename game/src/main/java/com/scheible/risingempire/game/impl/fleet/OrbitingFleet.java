package com.scheible.risingempire.game.impl.fleet;

import java.util.Map;
import java.util.Objects;

import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.system.SystemOrb;
import com.scheible.risingempire.util.jdk.Objects2;

import static com.scheible.risingempire.util.jdk.Objects2.toStringBuilder;

/**
 * @author sj
 */
public class OrbitingFleet extends Fleet {

	private final SystemOrb system;

	private final int arrivalRound;

	public OrbitingFleet(FleetId id, Player player, Map<DesignSlot, Integer> ships, SystemOrb system,
			int arrivalRound) {
		super(id, player, ships);

		this.system = system;
		this.arrivalRound = arrivalRound;
	}

	@Override
	public double getDestinationDistance() {
		return 0.0;
	}

	public SystemOrb getSystem() {
		return this.system;
	}

	@Override
	public Location getLocation() {
		return this.system.getLocation();
	}

	public int getArrivalRound() {
		return this.arrivalRound;
	}

	@Override
	public boolean equals(Object obj) {
		return Objects2.equals(this, obj, other -> Objects.equals(this.id, other.id));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public String toString() {
		return toStringBuilder(getClass()).add("id", id)
			.add("system", this.system)
			.add("ships", this.ships)
			.add("arrivalRound", this.arrivalRound)
			.toString();
	}

}
