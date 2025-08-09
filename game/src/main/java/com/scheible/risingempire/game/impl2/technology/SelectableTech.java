package com.scheible.risingempire.game.impl2.technology;

import java.util.List;
import java.util.Optional;

/**
 * @author sj
 */
public record SelectableTech(Optional<Tech> researched, List<Tech> next) {

}
