package com.scheible.risingempire.game.impl.fleet;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.game.impl.ship.ShipDesignProvider;
import com.scheible.risingempire.game.impl.system.System;

/**
 *
 * @author sj
 */
public class JourneyCalculator {

	private final Map<SystemId, System> systems;
	private final Map<FleetId, Fleet> fleets;
	private final ShipDesignProvider shipDesignProvider;
	private final double fleetSpeedFactor;

	public JourneyCalculator(final Map<SystemId, System> systems, final Map<FleetId, Fleet> fleets,
			final ShipDesignProvider shipDesignProvider, final double fleetSpeedFactor) {
		this.systems = Collections.unmodifiableMap(systems);
		this.fleets = Collections.unmodifiableMap(fleets);
		this.shipDesignProvider = shipDesignProvider;
		this.fleetSpeedFactor = fleetSpeedFactor;
	}

	public int calcFleetSpeed(final Player player, final Map<DesignSlot, Integer> ships) {
		return ships.entrySet().stream().filter(s -> s.getValue() > 0)
				.map(slot -> shipDesignProvider.get(player, slot.getKey()))
				.mapToInt(shipDesign -> warpToMapSpeed(shipDesign, fleetSpeedFactor)).min().getAsInt();
	}

	static int warpToMapSpeed(final ShipDesign shipDesign, final double fleetSpeedFactor) {
		return (int) (((shipDesign.getWarpSpeed() - 1) * 20 + 40) * fleetSpeedFactor);
	}

	public Optional<Integer> calcEta(final Player player, final FleetId fleetId, final SystemId destinationId,
			final Map<DesignSlot, Integer> ships, final int range) {
		final int destinationRange = systems.get(destinationId).calcRange(player, systems.values());

		if (!ships.isEmpty() && destinationRange <= range) {
			final int fleetSpeed = calcFleetSpeed(player, ships);
			final double distance = fleets.get(fleetId).getLocation()
					.getDistance(systems.get(destinationId).getLocation());

			return Optional.of((int) Math.ceil(distance / fleetSpeed));
		} else {
			return Optional.empty();
		}
	}
}
