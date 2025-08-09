package com.scheible.risingempire.game.impl2.technology;

import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.impl2.apiinternal.ResearchPoint;

/**
 * @author sj
 */
public record Tech(TechId id, String name, String description, int level, TechCategory category,
		ResearchPoint expense) {

}
