package com.scheible.risingempire.game.impl2.spaceforce.combat.weapon;

import com.scheible.risingempire.game.impl2.apiinternal.Damage;

/**
 * @author sj
 */
public record CombatMissile(String name, Damage damage, int rackSize) implements CombatWeapon {

}
