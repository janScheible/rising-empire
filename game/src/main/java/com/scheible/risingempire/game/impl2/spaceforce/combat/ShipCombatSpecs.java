package com.scheible.risingempire.game.impl2.spaceforce.combat;

import java.util.Map;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.spaceforce.combat.ShipCombatSpecsBuilder.ShipClassIdStage;
import com.scheible.risingempire.game.impl2.spaceforce.combat.weapon.CombatWeapon;

/**
 * @author sj
 */
@StagedRecordBuilder
public record ShipCombatSpecs(ShipClassId shipClassId, int hitPoints, int hitsAbsorbedByShield, int beamDefence,
		int missileDefence, int initiative, int combatSpeed, Map<CombatWeapon, Integer> weapons,
		boolean battleScanner) {

	public static ShipClassIdStage builder() {
		return ShipCombatSpecsBuilder.builder();
	}
}