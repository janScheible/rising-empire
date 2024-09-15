package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection;
import com.scheible.risingempire.game.api.view.system.StarType;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.webapp.adapter.frontend.dto.PlayerDto;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;

/**
 * @author sj
 */
class StarMapDto {

	StarBackgroundDto starBackground;

	StarSelectionDto starSelection;

	FleetSelectionDto fleetSelection;

	List<EntityModel<StarDto>> stars;

	Map<String, List<EntityModel<FleetDto>>> fleets;

	List<StarNotificationDto> starNotifications;

	RangesDto ranges;

	StarMapDto(int width, int height) {
		this.starBackground = new StarBackgroundDto(width, height);
	}

	static class StarBackgroundDto {

		final int width;

		final int height;

		StarBackgroundDto(int width, int height) {
			this.width = width;
			this.height = height;
		}

	}

	static class StarSelectionDto {

		final String id;

		final int x;

		final int y;

		Optional<ItineraryDto> itinerary;

		StarSelectionDto(SystemId id, int x, int y) {
			this.id = id.value();
			this.x = x;
			this.y = y;
		}

	}

	static class FleetSelectionDto {

		final String id;

		final int x;

		final int y;

		final boolean deployable;

		final boolean orbiting;

		final Optional<String> orbitingStarId;

		final boolean justLeaving;

		Optional<ItineraryDto> itinerary;

		FleetSelectionDto(FleetId id, int x, int y, boolean deployable, boolean orbiting,
				Optional<String> orbitingStarId, boolean justLeaving) {
			this.id = id.value();
			this.x = x;
			this.y = y;
			this.deployable = deployable;
			this.orbiting = orbiting;
			this.orbitingStarId = orbitingStarId;
			this.justLeaving = justLeaving;
		}

	}

	static class ItineraryDto {

		final int fleetX;

		final int fleetY;

		final int starX;

		final int starY;

		final boolean orbiting;

		final boolean justLeaving;

		final boolean inRange;

		final boolean relocation;

		ItineraryDto(int fleetX, int fleetY, int starX, int starY, boolean orbiting, boolean justLeaving,
				boolean inRange, boolean relocation) {
			this.fleetX = fleetX;
			this.fleetY = fleetY;
			this.starX = starX;
			this.starY = starY;
			this.orbiting = orbiting;
			this.justLeaving = justLeaving;
			this.inRange = inRange;
			this.relocation = relocation;
		}

	}

	static class StarDto {

		final String id;

		final Optional<String> name;

		final StarType type;

		final boolean small;

		final Optional<PlayerDto> playerColor;

		final Optional<PlayerDto> siegePlayerColor;

		final Optional<Integer> siegeProgress; // 0..100

		final int x;

		final int y;

		final Optional<ItineraryDto> relocation;

		StarDto(SystemId id, Optional<String> name, StarType type, boolean small, Optional<Player> playerColor,
				Optional<Player> siegePlayerColor, Optional<Integer> siegeProgress, int x, int y,
				Optional<ItineraryDto> relocation) {
			this.id = id.value();
			this.name = name;
			this.type = type;
			this.small = small;
			this.playerColor = playerColor.map(PlayerDto::fromPlayer);
			this.siegePlayerColor = siegePlayerColor.map(PlayerDto::fromPlayer);
			this.siegeProgress = siegeProgress;
			this.x = x;
			this.y = y;
			this.relocation = relocation;
		}

	}

	static class FleetDto {

		enum HorizontalDirectionDto {

			@JsonProperty("left")
			LEFT,

			@JsonProperty("right")
			RIGHT;

			static HorizontalDirectionDto fromHorizontalDirection(HorizontalDirection horizontalDirection) {
				return horizontalDirection != null ? HorizontalDirectionDto.valueOf(horizontalDirection.name()) : null;
			}

		}

		final String id;

		final PlayerDto playerColor;

		final Optional<Integer> previousX;

		final Optional<Integer> previousY;

		final Optional<Boolean> previousJustLeaving;

		final int x;

		final int y;

		final boolean orbiting;

		final boolean justLeaving;

		final Optional<Integer> speed;

		final Optional<HorizontalDirectionDto> horizontalDirection;

		final List<EntityModel<FleetDto>> fleetsBeforeArrival;

		FleetDto(FleetId id, Player playerColor, Optional<Integer> previousX, Optional<Integer> previousY,
				Optional<Boolean> previousJustLeaving, int x, int y, boolean orbiting, boolean justLeaving,
				Optional<Integer> speed, Optional<HorizontalDirection> horizontalDirection,
				List<EntityModel<FleetDto>> fleetsBeforeArrival) {
			this.id = id.value();
			this.playerColor = PlayerDto.fromPlayer(playerColor);
			this.previousX = previousX;
			this.previousY = previousY;
			this.previousJustLeaving = previousJustLeaving;
			this.x = x;
			this.y = y;
			this.orbiting = orbiting;
			this.justLeaving = justLeaving;
			this.speed = speed;
			this.horizontalDirection = horizontalDirection.map(HorizontalDirectionDto::fromHorizontalDirection);
			this.fleetsBeforeArrival = fleetsBeforeArrival;
		}

	}

	static class StarNotificationDto {

		final String starId;

		final int x;

		final int y;

		final String text;

		StarNotificationDto(SystemId starId, int x, int y, String text) {
			this.starId = starId.value();

			this.x = x;
			this.y = y;

			this.text = text;
		}

	}

	static class RangesDto {

		final int starMapWidth;

		final int starMapHeight;

		final PlayerDto playerColor;

		List<FleetRangeDto> fleetRanges = new ArrayList<>();

		List<ScannerRangeDto> colonyScannerRanges = new ArrayList<>();

		List<ScannerRangeDto> fleetScannerRanges = new ArrayList<>();

		RangesDto(int starMapWidth, int starMapHeight, Player playerColor) {
			this.starMapWidth = starMapWidth;
			this.starMapHeight = starMapHeight;
			this.playerColor = PlayerDto.fromPlayer(playerColor);
		}

		static class FleetRangeDto {

			final String id;

			final int centerX;

			final int centerY;

			final int radius;

			final int extendedRadius;

			FleetRangeDto(String id, int centerX, int centerY, int radius, int extendedRadius) {
				this.id = id;
				this.centerX = centerX;
				this.centerY = centerY;
				this.radius = radius;
				this.extendedRadius = extendedRadius;
			}

		}

		static class ScannerRangeDto {

			final String id;

			final int centerX;

			final int centerY;

			final int radius;

			ScannerRangeDto(String id, int centerX, int centerY, int radius) {
				this.id = id;
				this.centerX = centerX;
				this.centerY = centerY;
				this.radius = radius;
			}

		}

	}

}
