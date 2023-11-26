package com.scheible.risingempire.webapp.adapter.frontend;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.scheible.risingempire.webapp.adapter.frontend.dto.PlayerDto;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;

/**
 * @author sj
 */
record GameBrowserDto(EntityModel<GameLauncherDto> gameLauncher, List<EntityModel<RunningGameDto>> runningGames) {

	GameBrowserDto {
		runningGames = Collections.unmodifiableList(runningGames);
	}

	record GameLauncherDto(String defaultGameId, List<PlayerDto> playerColors) {

		GameLauncherDto {
			playerColors = Collections.unmodifiableList(playerColors);
		}
	}

	record RunningGameDto(String gameId, List<EntityModel<RunningGamePlayerDto>> players, int round) {

		RunningGameDto {
			players = Collections.unmodifiableList(players);
		}

		record RunningGamePlayerDto(PlayerDto playerColor, boolean interactive, Optional<String> playerSessionId,
				boolean canReceiveNotifications) {

		}

	}
}
