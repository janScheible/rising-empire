package com.scheible.risingempire.web.join;

import com.scheible.risingempire.game.common.Player;

/**
 *
 * @author sj
 */
public class PlayerHelper {

	public static Player resolvePlayer(String username) {
		String[] parts = username.split("@");
		return new Player(parts[0], parts[1]);
	}
	
	public static String generateUsername(String leaderName, String nation) {
		return leaderName + "@" + nation;
	}
}
