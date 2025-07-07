package com.scheible.risingempire.game.impl2.ship;

import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.ship.ShipDesignBuilder.IdStage;
import com.scheible.risingempire.game.impl2.ship.component.Armor;
import com.scheible.risingempire.game.impl2.ship.component.Computer;
import com.scheible.risingempire.game.impl2.ship.component.Ecm;
import com.scheible.risingempire.game.impl2.ship.component.Engine;
import com.scheible.risingempire.game.impl2.ship.component.Maneuver;
import com.scheible.risingempire.game.impl2.ship.component.Shield;
import com.scheible.risingempire.game.impl2.ship.special.BattleScanner;
import com.scheible.risingempire.game.impl2.ship.special.Special;
import com.scheible.risingempire.game.impl2.ship.weapon.Weapon;

/**
 * @author sj
 */
@StagedRecordBuilder
public record ShipDesign(ShipClassId id, String name, ShipSize size, int look, Computer computer, Shield shield,
		Ecm ecm, Armor armor, Engine engine, Maneuver maneuver, Map<Weapon, Integer> weapons, Set<Special> specials) {

	public static IdStage builder() {
		return ShipDesignBuilder.builder();
	}

	public int hitPoints() {
		return (int) (ShipSizeBaseValues.baseHits(this.size) * this.armor.factor());
	}

	public int hitsAbsorbedByShield() {
		return this.shield.level();
	}

	public int beamDefence() {
		return ShipSizeBaseValues.baseDefense(this.size) + this.maneuver.level();
	}

	public int missileDefence() {
		return beamDefence() + this.ecm.level();
	}

	public int attackLevel() {
		int battleScannerBonus = this.specials.stream()
			.anyMatch(special -> special.getClass().equals(BattleScanner.class)) ? 1 : 0;
		return this.computer.level() + battleScannerBonus;
	}

	public int initiative() {
		int battleScannerBonus = this.specials.stream()
			.anyMatch(special -> special.getClass().equals(BattleScanner.class)) ? 3 : 0;
		return attackLevel() + beamDefence() + battleScannerBonus;
	}

	public int combatSpeed() {
		return (this.engine.warp() + 2) / 2;
	}
}
