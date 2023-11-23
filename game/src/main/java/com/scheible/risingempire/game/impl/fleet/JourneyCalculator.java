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
 * @author sj
 */
public class JourneyCalculator {

	private final Map<SystemId, System> systems;

	private final Map<FleetId, Fleet> fleets;

	private final ShipDesignProvider shipDesignProvider;

	private final double fleetSpeedFactor;

	public JourneyCalculator(Map<SystemId, System> systems, Map<FleetId, Fleet> fleets,
			ShipDesignProvider shipDesignProvider, double fleetSpeedFactor) {
		this.systems = Collections.unmodifiableMap(systems);
		this.fleets = Collections.unmodifiableMap(fleets);
		this.shipDesignProvider = shipDesignProvider;
		this.fleetSpeedFactor = fleetSpeedFactor;
	}

	public int calcFleetSpeed(Player player, Map<DesignSlot, Integer> ships) {
		return ships.entrySet()
			.stream()
			.filter(s -> s.getValue() > 0)
			.map(slot -> this.shipDesignProvider.get(player, slot.getKey()))
			.mapToInt(shipDesign -> warpToMapSpeed(shipDesign, this.fleetSpeedFactor))
			.min()
			.getAsInt();
	}

	static int warpToMapSpeed(ShipDesign shipDesign, double fleetSpeedFactor) {
		return (int) (((shipDesign.getWarpSpeed() - 1) * 20 + 40) * fleetSpeedFactor);
	}

	public Optional<Integer> calcEta(Player player, FleetId fleetId, SystemId destinationId,
			Map<DesignSlot, Integer> ships, int range) {
		int destinationRange = this.systems.get(destinationId).calcRange(player, this.systems.values());

		if (!ships.isEmpty() && destinationRange <= range) {
			int fleetSpeed = calcFleetSpeed(player, ships);
			double distance = this.fleets.get(fleetId)
				.getLocation()
				.getDistance(this.systems.get(destinationId).getLocation());

			return Optional.of((int) Math.ceil(distance / fleetSpeed));
		}
		else {
			return Optional.empty();
		}
	}

}
