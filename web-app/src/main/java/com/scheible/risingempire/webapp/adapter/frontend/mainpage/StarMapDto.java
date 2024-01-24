package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection;
import com.scheible.risingempire.game.api.view.system.StarType;
import com.scheible.risingempire.game.api.view.universe.Player;
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

	List<EntityModel<FleetDto>> fleets;

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

		final int x;

		final int y;

		Optional<ItineraryDto> itinerary;

		StarSelectionDto(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}

	static class FleetSelectionDto {

		final int x;

		final int y;

		final boolean orbiting;

		final boolean justLeaving;

		Optional<ItineraryDto> itinerary;

		FleetSelectionDto(int x, int y, boolean orbiting, boolean justLeaving) {
			this.x = x;
			this.y = y;
			this.orbiting = orbiting;
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

		ItineraryDto(int fleetX, int fleetY, int starX, int starY, boolean orbiting, boolean justLeaving,
				boolean inRange) {
			this.fleetX = fleetX;
			this.fleetY = fleetY;
			this.starX = starX;
			this.starY = starY;
			this.orbiting = orbiting;
			this.justLeaving = justLeaving;
			this.inRange = inRange;
		}

	}

	static class StarDto {

		final String id;

		Optional<String> name;

		final StarType type;

		final boolean small;

		Optional<PlayerDto> playerColor;

		Optional<PlayerDto> siegePlayerColor;

		Optional<Integer> siegeProgress; // 0..100

		final int x;

		final int y;

		StarDto(String id, Optional<String> name, StarType type, boolean small, Optional<Player> playerColor,
				Optional<Player> siegePlayerColor, Optional<Integer> siegeProgress, int x, int y) {
			this.id = id;
			this.name = name;
			this.type = type;
			this.small = small;
			this.playerColor = playerColor.map(PlayerDto::fromPlayer);
			this.siegePlayerColor = siegePlayerColor.map(PlayerDto::fromPlayer);
			this.siegeProgress = siegeProgress;
			this.x = x;
			this.y = y;
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

		// TODO later...
		// final List<FleetDto> detachments;

		FleetDto(String id, Player playerColor, Optional<Integer> previousX, Optional<Integer> previousY,
				Optional<Boolean> previousJustLeaving, int x, int y, boolean orbiting, boolean justLeaving,
				Optional<Integer> speed, Optional<HorizontalDirection> horizontalDirection,
				List<EntityModel<FleetDto>> fleetsBeforeArrival) {
			this.id = id;
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

		StarNotificationDto(String starId, int x, int y, String text) {
			this.starId = starId;

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
