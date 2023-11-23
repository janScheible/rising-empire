package com.scheible.risingempire.game.impl.spacecombat.resolver.predetermined;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.impl.fleet.DeployedFleet;
import com.scheible.risingempire.game.impl.fleet.OrbitingFleet;
import com.scheible.risingempire.game.impl.fleet.SpaceCombatResolver;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.ship.ShipDesignProvider;
import com.scheible.risingempire.game.impl.spacecombat.FireExchange;
import com.scheible.risingempire.game.impl.spacecombat.SpaceCombat;

/**
 * @author sj
 */
public class KnownInAdvanceWinnerSpaceCombatResolver implements SpaceCombatResolver {

	private final Outcome outcome;

	public KnownInAdvanceWinnerSpaceCombatResolver(Outcome outcome) {
		this.outcome = outcome;
	}

	@Override
	public SpaceCombat resolve(SystemId systemId, OrbitingFleet defending, DeployedFleet attacking,
			ShipDesignProvider shipDesignProvider) {
		return new SpaceCombat(systemId, 1, attacking.getPlayer(),
				new FleetBeforeArrival(attacking.getId(), attacking.getHorizontalDirection(), attacking.getSpeed()),
				attacking.getShips(), this.outcome == Outcome.ATTACKER_WON ? Map.of() : toAllLost(attacking.getShips()),
				defending.getPlayer(), defending.getId(), defending.getShips(),
				this.outcome == Outcome.DEFENDER_WON ? toAllLost(defending.getShips()) : Map.of(), this.outcome);
	}

	static Map<DesignSlot, List<FireExchange>> toAllLost(Map<DesignSlot, Integer> shipCounts) {
		return shipCounts.entrySet()
			.stream()
			.map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), List.of(new FireExchange(0, 42, 0, 0))))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

}
