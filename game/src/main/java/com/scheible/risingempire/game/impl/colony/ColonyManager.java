package com.scheible.risingempire.game.impl.colony;

import java.util.Map;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;

/**
 * @author sj
 */
public interface ColonyManager {

	ShipTypeView nextShipType(Player player, ColonyId colonyId);

	Map<ProductionArea, Integer> adjustRatio(Player player, ColonyId id, ProductionArea area, int percentage);

}
