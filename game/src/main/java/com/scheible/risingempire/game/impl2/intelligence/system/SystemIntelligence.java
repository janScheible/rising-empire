package com.scheible.risingempire.game.impl2.intelligence.system;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.intelligence.system.SystemReconReport.ColonyReconReport;

/**
 * @author sj
 */
public class SystemIntelligence {

	private final ArrivedFleetsProvider arrivedFleetsProvider;

	private final ColonyIntelProvider colonyIntelProvider;

	private final Map<Player, Map<Position, SystemIntel>> knownSystems = new HashMap<>();

	public SystemIntelligence(ArrivedFleetsProvider arrivedFleetsProvider, ColonyIntelProvider colonyProvider) {
		this.arrivedFleetsProvider = arrivedFleetsProvider;
		this.colonyIntelProvider = colonyProvider;
	}

	public void recon(Round round) {
		Map<Player, Set<Position>> allArrivedFleets = this.arrivedFleetsProvider.arrivedFleets();
		for (Player player : allArrivedFleets.keySet()) {
			Set<Position> arrivedFleets = allArrivedFleets.get(player);
			for (Position arrivedFleet : arrivedFleets) {
				SystemIntel systemIntel = this.knownSystems.getOrDefault(player, Map.of()).get(arrivedFleet);

				Round firstSeen = systemIntel == null ? round : systemIntel.firstSeen();
				this.knownSystems.computeIfAbsent(player, _ -> new HashMap<>())
					.put(arrivedFleet,
							new SystemIntel(firstSeen, round, this.colonyIntelProvider.colony(player, arrivedFleet)));
			}
		}
	}

	public boolean justExplored(Player player, Round round, Position system) {
		return this.knownSystems.getOrDefault(player, Map.of())
			.entrySet()
			.stream()
			.anyMatch(e -> e.getKey().equals(system) && e.getValue().firstSeen().equals(round));
	}

	public SystemReconReport systemReconReport(Player player, Position system) {
		Optional<Entry<Position, SystemIntel>> systemIntelEntry = this.knownSystems.getOrDefault(player, Map.of())
			.entrySet()
			.stream()
			.filter(e -> e.getKey().equals(system))
			.findFirst();

		return new SystemReconReport(systemIntelEntry.isPresent(),
				systemIntelEntry.map(Entry::getValue)
					.flatMap(SystemIntel::colonyIntel)
					.map(ci -> new ColonyReconReport(ci.player(), ci.race(), ci.population())));
	}

}
