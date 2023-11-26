package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.Optional;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;

/**
 * @author sj
 */
public class MainPage {

	static class StarMapFleetBeforeArrival {

		final FleetId id;

		final Optional<HorizontalDirection> horizontalDirection;

		final Optional<Integer> speed;

		StarMapFleetBeforeArrival(FleetId id, Optional<HorizontalDirection> horizontalDirection,
				Optional<Integer> speed) {
			this.id = id;
			this.horizontalDirection = horizontalDirection;
			this.speed = speed;
		}

	}

	static class StarMapFleet {

		final FleetId id;

		final Player player;

		final int x;

		final int y;

		final boolean orbiting;

		final boolean justLeaving;

		final Optional<SystemId> destination;

		final Optional<Integer> speed;

		final Optional<HorizontalDirection> horizontalDirection;

		StarMapFleet(FleetId id, Player player, int x, int y, boolean orbiting, boolean justLeaving,
				Optional<SystemId> destination, Optional<Integer> speed,
				Optional<HorizontalDirection> horizontalDirection) {
			this.id = id;
			this.player = player;
			this.x = x;
			this.y = y;

			this.orbiting = orbiting;
			this.justLeaving = justLeaving;

			this.destination = destination;

			this.speed = speed;
			this.horizontalDirection = horizontalDirection;
		}

		static StarMapFleet createArrivingSpaceCombatFakeFleet(FleetId fleetId, Player player,
				SystemId destinationSystemId, int speed, HorizontalDirection horizontalDirection, int x, int y) {
			return new StarMapFleet(fleetId, player, x, y, false, false, Optional.of(destinationSystemId),
					Optional.of(speed), Optional.of(horizontalDirection));
		}

		static StarMapFleet createOrbitingSpaceCombatFakeFleet(FleetId fleetId, Player player, int x, int y) {
			return new StarMapFleet(fleetId, player, x, y, true, false, Optional.empty(), Optional.empty(),
					Optional.empty());
		}

	}

}
