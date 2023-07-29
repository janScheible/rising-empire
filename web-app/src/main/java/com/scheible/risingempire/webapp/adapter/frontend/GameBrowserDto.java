package com.scheible.risingempire.webapp.adapter.frontend;

import com.scheible.risingempire.webapp.adapter.frontend.dto.PlayerDto;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author sj
 */
record GameBrowserDto(EntityModel<GameLauncherDto> gameLauncher, List<EntityModel<RunningGameDto>> runningGames) {

	record GameLauncherDto(String defaultGameId, List<PlayerDto> playerColors) {
		
		GameLauncherDto {
			playerColors = Collections.unmodifiableList(playerColors);
		}
	}

	record RunningGameDto(String gameId, List<EntityModel<RunningGamePlayerDto>> players) {
		
		record RunningGamePlayerDto(PlayerDto playerColor, boolean interactive, @Nullable String playerSessionId,
				boolean canReceiveNotifications) {
			
		}
		
		RunningGameDto {
			players = Collections.unmodifiableList(players);
		}
	}

	GameBrowserDto {
		runningGames = Collections.unmodifiableList(runningGames);
	}
}
