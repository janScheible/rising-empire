package com.scheible.risingempire.game.impl.spacecombat.resolver.predetermined;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrivalBuilder;
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
 * Space combat with predetermined outcome. If a fleet survives all ship counts are
 * halved.
 *
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
				FleetBeforeArrivalBuilder.builder()
					.id(attacking.getId())
					.horizontalDirection(attacking.getHorizontalDirection())
					.speed(attacking.getSpeed())
					.location(attacking.getPreviousLocation())
					.justLeaving(attacking.isPreviousJustLeaving())
					.build(),
				attacking.getShips(),
				this.outcome == Outcome.DEFENDER_WON ? changeShipCount(attacking.getShips(), count -> 0)
						: changeShipCount(attacking.getShips(), count -> count / 2),
				defending.getPlayer(), defending.getId(), defending.getShips(),
				this.outcome == Outcome.ATTACKER_WON ? changeShipCount(defending.getShips(), (count) -> 0)
						: changeShipCount(defending.getShips(), (count) -> count / 2),
				this.outcome);
	}

	static Map<DesignSlot, List<FireExchange>> changeShipCount(Map<DesignSlot, Integer> shipCounts,
			Function<Integer, Integer> calculation) {
		return shipCounts.entrySet()
			.stream()
			.map(e -> Map.entry(e.getKey(), List.of(new FireExchange(0, 42, 100, calculation.apply(e.getValue())))))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

}
