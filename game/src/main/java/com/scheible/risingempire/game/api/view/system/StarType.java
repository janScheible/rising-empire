package com.scheible.risingempire.game.api.view.system;

import com.scheible.risingempire.util.SeededRandom;

/**
 * The spectral type of a star in a system.
 *
 * @author sj
 */
public enum StarType {

	YELLOW, RED, GREEN, BLUE, WHITE, PURPLE;

	public static StarType random(SeededRandom random) {
		StarType[] values = StarType.values();
		return values[random.nextInt(0, values.length)];
	}

}
