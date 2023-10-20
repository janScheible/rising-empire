package com.scheible.risingempire.game.impl.tech;

import java.util.List;
import java.util.Map;

import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.api.view.universe.Player;

/**
 *
 * @author sj
 */
public interface TechManager {

	List<List<Map.Entry<TechId, String>>> getSelectTechs(Player player);

	void selectTech(Player player, TechId techId);
}
