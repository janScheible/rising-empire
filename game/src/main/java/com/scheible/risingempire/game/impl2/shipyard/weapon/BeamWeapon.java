package com.scheible.risingempire.game.impl2.shipyard.weapon;

import com.scheible.risingempire.game.impl2.apiinternal.Damage;

/**
 * @author sj
 */
public record BeamWeapon(String name, Damage damage) implements Weapon {

}
