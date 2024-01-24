package com.scheible.risingempire.game.impl.fleet;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Location;
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

	private final ShipDesignProvider shipDesignProvider;

	private final double fleetSpeedFactor;

	public JourneyCalculator(Map<SystemId, System> systems, ShipDesignProvider shipDesignProvider,
			double fleetSpeedFactor) {
		this.systems = Collections.unmodifiableMap(systems);
		this.shipDesignProvider = shipDesignProvider;
		this.fleetSpeedFactor = fleetSpeedFactor;
	}

	public int calcFleetSpeed(Player player, Map<DesignSlot, Integer> ships) {
		return warpToMapSpeed(getFleetSpeed(player, ships), this.fleetSpeedFactor);
	}

	private int getFleetSpeed(Player player, Map<DesignSlot, Integer> ships) {
		return ships.entrySet()
			.stream()
			.filter(s -> s.getValue() > 0)
			.map(slot -> this.shipDesignProvider.get(player, slot.getKey()))
			.mapToInt(ShipDesign::getWarpSpeed)
			.min()
			.getAsInt();
	}

	static int warpToMapSpeed(int warpSpeed, double fleetSpeedFactor) {
		return (int) (((warpSpeed - 1) * 20 + 40) * fleetSpeedFactor);
	}

	public Optional<Integer> calcEta(Player player, Location origin, SystemId destinationId,
			Map<DesignSlot, Integer> ships, int range) {
		if (!ships.isEmpty()) {
			return calcEta(player, origin, destinationId, getFleetSpeed(player, ships), range);
		}
		else {
			return Optional.empty();
		}
	}

	public Optional<Integer> calcEta(Player player, Location origin, SystemId destinationId, int warpSpeed, int range) {
		int destinationRange = this.systems.get(destinationId).calcRange(player, this.systems.values());

		if (destinationRange <= range) {
			double distance = origin.getDistance(this.systems.get(destinationId).getLocation());
			int fleetSpeed = warpToMapSpeed(warpSpeed, this.fleetSpeedFactor);
			return Optional.of((int) Math.ceil(distance / fleetSpeed));
		}
		else {
			return Optional.empty();
		}
	}

}
