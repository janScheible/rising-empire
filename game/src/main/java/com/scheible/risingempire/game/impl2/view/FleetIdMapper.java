package com.scheible.risingempire.game.impl2.view;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;

/**
 * @author sj
 */
public class FleetIdMapper {

	public static FleetId toFleetId(Player player, Location location) {
		return switch (location) {
			case Orbit orbit -> toFleetId(player, orbit.system());
			case Itinerary itinerary -> toFleetId(player, itinerary.origin(), itinerary.destination(),
					itinerary.dispatchment(), itinerary.speed());
		};
	}

	public static FleetId toFleetId(Player player, Position origin, Position destination, Round dispatchment,
			Speed speed) {
		return new FleetId("f" + player.name().substring(0, 1) + origin.toPlainString() + "->"
				+ destination.toPlainString() + "@" + dispatchment.quantity() + "w/" + speed.toPlainString());
	}

	public static FleetId toFleetId(Player player, Position origin) {
		return new FleetId("f" + player.name().substring(0, 1) + origin.toPlainString());
	}

	public static DomainFleetId fromFleetId(FleetId fleetId) {
		if (!fleetId.value().contains("-")) {
			return new OrbitingFleetId(Position.fromPlainString(fleetId.value().substring(2)));
		}
		else {
			String[] firstLevelParts = fleetId.value().split("@");
			String[] originDestinationParts = firstLevelParts[0].split("->");
			String[] currentSpeedParts = firstLevelParts[1].split("w/");

			return new DeployedFleetId(Position.fromPlainString(originDestinationParts[0].substring(2)),
					Position.fromPlainString(originDestinationParts[1]),
					new Round(Integer.parseInt(currentSpeedParts[0])), Speed.fromPlainString(currentSpeedParts[1]));
		}
	}

	public sealed interface DomainFleetId {

	}

	public record OrbitingFleetId(Position system) implements DomainFleetId {

	}

	public record DeployedFleetId(Position origin, Position destination, Round dispatchment,
			Speed speed) implements DomainFleetId {

	}

}
