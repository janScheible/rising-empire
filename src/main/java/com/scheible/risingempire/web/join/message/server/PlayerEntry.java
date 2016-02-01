package com.scheible.risingempire.web.join.message.server;

import com.scheible.risingempire.game.common.Player;
import com.scheible.risingempire.web.appearance.AvailablePlayer;
import com.scheible.risingempire.web.appearance.BrowserColor;
import com.scheible.risingempire.web.join.PlayerHelper;
import static com.scheible.risingempire.web.join.PlayerHelper.resolvePlayer;
import java.awt.Color;

/**
 *
 * @author sj
 */
public class PlayerEntry {
	
	public enum State {
		ACTIVE, DETACHED, AI, NON_PARTICIPATING;
	}
	
	private final String leaderName;
	private final String nation;
	private final String username;
	private final State state;
	private final String color;

	private PlayerEntry(String leaderName, String nation, State state, Color color) {
		this.leaderName = leaderName;
		this.nation = nation;
		this.username = PlayerHelper.generateUsername(leaderName, nation);
		this.state = state;
		this.color = new BrowserColor(color).getHex();
	}
	
	public static PlayerEntry create(String username, PlayerEntry.State state) {
		Player player = resolvePlayer(username);
		return create(player.getName(), player.getNation(), state);
	}
	
	public static PlayerEntry create(String leaderName, String nation, PlayerEntry.State state) {
		return new PlayerEntry(leaderName, nation, state,
				AvailablePlayer.find(leaderName, nation).getColor());
	}	
}
