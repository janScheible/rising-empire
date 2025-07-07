package com.scheible.risingempire.game.impl2.ship.weapon;

import com.scheible.risingempire.game.impl2.apiinternal.Damage;

/**
 * @author sj
 */
public record Missile(String name, Damage damage, int rackSize) implements Weapon {

}
