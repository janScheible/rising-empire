package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * @author sj
 */
public class MainPage {

	static class StarMapFleetBeforeArrival {

		final FleetId id;

		@Nullable
		HorizontalDirection horizontalDirection;

		@Nullable
		final Integer speed;

		StarMapFleetBeforeArrival(final FleetId id, @Nullable final HorizontalDirection horizontalDirection,
				@Nullable final Integer speed) {
			this.id = id;
			this.horizontalDirection = horizontalDirection;
			this.speed = speed;
		}

		public FleetId getId() {
			return id;
		}

		@Nullable
		public HorizontalDirection getHorizontalDirection() {
			return horizontalDirection;
		}

		@Nullable
		public Integer getSpeed() {
			return speed;
		}

	}

	static class StarMapFleet {

		final FleetId id;

		final Player player;

		final int x;

		final int y;

		final boolean orbiting;

		final boolean justLeaving;

		@Nullable
		final SystemId destination;

		@Nullable
		final Integer speed;

		@Nullable
		final HorizontalDirection horizontalDirection;

		StarMapFleet(final FleetId id, final Player player, final int x, final int y, final boolean orbiting,
				final boolean justLeaving, @Nullable final SystemId destination, @Nullable final Integer speed,
				@Nullable final HorizontalDirection horizontalDirection) {
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

		static StarMapFleet createArrivingSpaceCombatFakeFleet(final FleetId fleetId, final Player player,
				final SystemId destinationSystemId, final int speed, final HorizontalDirection horizontalDirection,
				final int x, final int y) {
			return new StarMapFleet(fleetId, player, x, y, false, false, destinationSystemId, speed,
					horizontalDirection);
		}

		static StarMapFleet createOrbitingSpaceCombatFakeFleet(final FleetId fleetId, final Player player, final int x,
				final int y) {
			return new StarMapFleet(fleetId, player, x, y, true, false, null, null, null);
		}

	}

}
