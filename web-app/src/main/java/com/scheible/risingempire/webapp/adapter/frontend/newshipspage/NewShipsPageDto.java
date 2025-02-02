package com.scheible.risingempire.webapp.adapter.frontend.newshipspage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.webapp.adapter.frontend.dto.PlayerDto;

/**
 * @author sj
 */
class NewShipsPageDto {

	@JsonProperty(value = "@type")
	final String type = getClass().getSimpleName();

	final PlayerDto playerColor;

	final int round;

	final List<NewShipDto> newShips;

	NewShipsPageDto(Player playerColor, int round, List<NewShipDto> newShips) {
		this.playerColor = PlayerDto.fromPlayer(playerColor);
		this.round = round;
		this.newShips = newShips;
	}

	static class NewShipDto {

		final String name;

		final ShipSize size;

		final int count;

		NewShipDto(String name, ShipSize size, int count) {
			this.name = name;
			this.size = size;
			this.count = count;
		}

		String name() {
			return this.name;
		}

	}

}
