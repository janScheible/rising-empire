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
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * @author sj
 */
class StarMapDto {

	static class StarBackgroundDto {

		final int width;

		final int height;

		StarBackgroundDto(final int width, final int height) {
			this.width = width;
			this.height = height;
		}

	}

	static class StarSelectionDto {

		final int x;

		final int y;

		StarSelectionDto(final int x, final int y) {
			this.x = x;
			this.y = y;
		}

	}

	static class FleetSelectionDto {

		final int x;

		final int y;

		final boolean orbiting;

		final boolean justLeaving;

		FleetSelectionDto(final int x, final int y, final boolean orbiting, final boolean justLeaving) {
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

		ItineraryDto(final int fleetX, final int fleetY, final int starX, final int starY, final boolean orbiting,
				final boolean justLeaving, final boolean inRange) {
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

		@Nullable
		final String name;

		final StarType type;

		final boolean small;

		@Nullable
		final PlayerDto playerColor;

		@Nullable
		final PlayerDto siegePlayerColor;

		@Nullable
		final Integer siegeProgress; // 0..100

		final int x;

		final int y;

		StarDto(final String id, final Optional<String> name, final StarType type, final boolean small,
				final Optional<Player> playerColor, final Optional<Player> siegePlayerColor,
				final Optional<Integer> siegeProgress, final int x, final int y) {
			this.id = id;
			this.name = name.orElse(null);
			this.type = type;
			this.small = small;
			this.playerColor = playerColor.map(PlayerDto::fromPlayer).orElse(null);
			this.siegePlayerColor = siegePlayerColor.map(PlayerDto::fromPlayer).orElse(null);
			this.siegeProgress = siegeProgress.orElse(null);
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

			public static HorizontalDirectionDto fromHorizontalDirection(
					final HorizontalDirection horizontalDirection) {
				return horizontalDirection != null ? HorizontalDirectionDto.valueOf(horizontalDirection.name()) : null;
			}

		}

		final String id;

		final PlayerDto playerColor;

		final int x;

		final int y;

		final boolean orbiting;

		final boolean justLeaving;

		final Integer speed;

		final HorizontalDirectionDto horizontalDirection;

		FleetDto(final String id, final Player playerColor, final int x, final int y, final boolean orbiting,
				final boolean justLeaving, final Integer speed, final HorizontalDirection horizontalDirection) {
			this.id = id;
			this.playerColor = PlayerDto.fromPlayer(playerColor);
			this.x = x;
			this.y = y;
			this.orbiting = orbiting;
			this.justLeaving = justLeaving;
			this.speed = speed;
			this.horizontalDirection = HorizontalDirectionDto.fromHorizontalDirection(horizontalDirection);
		}

	}

	static class StarNotificationDto {

		final int x;

		final int y;

		final String text;

		int starMapWidth;

		int starMapHeight;

		StarNotificationDto(final int x, final int y, final String text) {
			this.x = x;
			this.y = y;
			this.text = text;
		}

	}

	static class ScrollToDto {

		final int x;

		final int y;

		final boolean center;

		ScrollToDto(final int x, final int y, final boolean center) {
			this.x = x;
			this.y = y;
			this.center = center;
		}

	}

	static class RangesDto {

		final int starMapWidth;

		final int starMapHeight;

		final PlayerDto playerColor;

		List<FleetRangeDto> fleetRanges = new ArrayList<>();

		List<ScannerRangeDto> colonyScannerRanges = new ArrayList<>();

		List<ScannerRangeDto> fleetScannerRanges = new ArrayList<>();

		RangesDto(final int starMapWidth, final int starMapHeight, final Player playerColor) {
			this.starMapWidth = starMapWidth;
			this.starMapHeight = starMapHeight;
			this.playerColor = PlayerDto.fromPlayer(playerColor);
		}

	}

	static class FleetRangeDto {

		final String id;

		final int centerX;

		final int centerY;

		final int radius;

		final int extendedRadius;

		FleetRangeDto(final String id, final int centerX, final int centerY, final int radius,
				final int extendedRadius) {
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

		ScannerRangeDto(final String id, final int centerX, final int centerY, final int radius) {
			this.id = id;
			this.centerX = centerX;
			this.centerY = centerY;
			this.radius = radius;
		}

	}

	final boolean miniMap;

	final boolean fleetMovements;

	StarBackgroundDto starBackground;

	StarSelectionDto starSelection;

	FleetSelectionDto fleetSelection;

	ItineraryDto itinerary;

	List<EntityModel<StarDto>> stars;

	List<EntityModel<FleetDto>> fleets;

	EntityModel<StarNotificationDto> starNotification;

	ScrollToDto scrollTo;

	RangesDto ranges;

	StarMapDto(final int width, final int height, final boolean miniMap, final boolean fleetMovements) {
		this.starBackground = new StarBackgroundDto(width, height);
		this.miniMap = miniMap;
		this.fleetMovements = fleetMovements;
	}

	void setStarNotification(final EntityModel<StarNotificationDto> notificationModel) {
		scrollTo = new ScrollToDto(notificationModel.getContent().x, notificationModel.getContent().y, true);
		notificationModel.getContent().starMapWidth = starBackground.width;
		notificationModel.getContent().starMapHeight = starBackground.height;
		starNotification = notificationModel;

	}

}
