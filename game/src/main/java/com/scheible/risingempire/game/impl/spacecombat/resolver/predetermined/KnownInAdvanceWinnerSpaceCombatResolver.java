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

import static com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome.ATTACKER_WON;
import static com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome.DEFENDER_WON;
import static java.util.Collections.emptyMap;

/**
 * @author sj
 */
public class KnownInAdvanceWinnerSpaceCombatResolver implements SpaceCombatResolver {

	private final Outcome outcome;

	public KnownInAdvanceWinnerSpaceCombatResolver(final Outcome outcome) {
		this.outcome = outcome;
	}

	@Override
	public SpaceCombat resolve(final SystemId systemId, final OrbitingFleet defending, final DeployedFleet attacking,
			final ShipDesignProvider shipDesignProvider) {
		return new SpaceCombat(systemId, 1, attacking.getPlayer(),
				new FleetBeforeArrival(attacking.getId(), attacking.getHorizontalDirection(), attacking.getSpeed()),
				attacking.getShips(), outcome == ATTACKER_WON ? emptyMap() : toAllLost(attacking.getShips()),
				defending.getPlayer(), defending.getId(), defending.getShips(),
				outcome == DEFENDER_WON ? toAllLost(defending.getShips()) : emptyMap(), outcome);
	}

	static Map<DesignSlot, List<FireExchange>> toAllLost(final Map<DesignSlot, Integer> shipCounts) {
		return shipCounts.entrySet()
			.stream()
			.map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), List.of(new FireExchange(0, 42, 0, 0))))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

}
