package com.scheible.risingempire.webapp.adapter.frontend.dto;

import com.scheible.risingempire.game.api.universe.Race;

/**
 * @author sj
 */
public enum RaceDto {

	BLYZARIANS, DRACONILITHS, KRYLOQUIANS, LUMERISKS, MYXALOR, OLTHARIEN, QALTRUVIAN, VORTELUXIAN, XELIPHARI, ZYNTHORAX;

	public static RaceDto fromRace(Race race) {
		return RaceDto.valueOf(race.name());
	}

}
