package com.scheible.risingempire.game.impl.fleet;

import static com.scheible.risingempire.util.jdk.Objects2.toStringBuilder;

import java.util.Map;
import java.util.Objects;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.system.SystemOrb;
import com.scheible.risingempire.util.jdk.Objects2;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
@SuppressFBWarnings(value = "UEC_USE_ENUM_COLLECTIONS", justification = "Can't be used with unmodifiableMap(...).")
public class OrbitingFleet extends Fleet {

	private final SystemOrb system;
	private final int arrivalRound;

	public OrbitingFleet(final FleetId id, final Player player, final Map<DesignSlot, Integer> ships,
			final SystemOrb system, final int arrivalRound) {
		super(id, player, ships);

		this.system = system;
		this.arrivalRound = arrivalRound;
	}

	@Override
	public double getDestinationDistance() {
		return 0.0;
	}

	public SystemOrb getSystem() {
		return system;
	}

	@Override
	public Location getLocation() {
		return system.getLocation();
	}

	public int getArrivalRound() {
		return arrivalRound;
	}

	@SuppressFBWarnings(value = "EQ_UNUSUAL", justification = "Object2.equals() is allowed.")
	@Override
	public boolean equals(final Object obj) {
		return Objects2.equals(this, obj, other -> Objects.equals(id, other.id));
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return toStringBuilder(getClass()).add("id", id).add("system", system).add("ships", ships)
				.add("arrivalRound", arrivalRound).toString();
	}
}
