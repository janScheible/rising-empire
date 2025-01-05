package com.scheible.risingempire.game.impl2.intelligence.system;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.intelligence.system.ColonyIntelProvider.ColonyIntel;
import com.scheible.risingempire.game.impl2.intelligence.system.SystemReconReport.ColonyReconReport;

/**
 * @author sj
 */
public class SystemIntelligence {

	private final OrbitingFleetsProvider orbitingFleetsProviderAdapter;

	private final ColonyIntelProvider colonyIntelProvider;

	private final Map<Player, Map<Position, SystemIntel>> knownSystems = new HashMap<>();

	public SystemIntelligence(OrbitingFleetsProvider orbitingFleetsProviderAdapter,
			ColonyIntelProvider colonyProvider) {
		this.orbitingFleetsProviderAdapter = orbitingFleetsProviderAdapter;
		this.colonyIntelProvider = colonyProvider;
	}

	public void recon(Round round) {
		Map<Player, Set<Position>> allOrbitingFleets = this.orbitingFleetsProviderAdapter.orbitingFleets();
		for (Player player : allOrbitingFleets.keySet()) {
			Set<Position> orbitingFleets = allOrbitingFleets.get(player);
			for (Position orbitingFleet : orbitingFleets) {
				SystemIntel systemIntel = this.knownSystems.getOrDefault(player, Map.of()).get(orbitingFleet);

				Round firstSeen = systemIntel == null ? round : systemIntel.firstSeen();
				this.knownSystems.computeIfAbsent(player, _ -> new HashMap<>())
					.put(orbitingFleet, new SystemIntel(firstSeen, round,
							this.colonyIntelProvider.colony(orbitingFleet).filter(c -> c.player() != player)));
			}
		}
	}

	public boolean justExplored(Player player, Optional<Round> round, Position system) {
		if (player.equals(this.colonyIntelProvider.colony(system).map(ColonyIntel::player).orElse(null))) {
			return false;
		}
		else {
			return this.knownSystems.getOrDefault(player, Map.of())
				.entrySet()
				.stream()
				.anyMatch(e -> e.getKey().equals(system) && e.getValue().firstSeen().equals(round.orElse(null)));
		}
	}

	public SystemReconReport systemReconReport(Player player, Optional<Round> round, Position system) {
		Optional<Entry<Position, SystemIntel>> systemIntelEntry = this.knownSystems.getOrDefault(player, Map.of())
			.entrySet()
			.stream()
			.filter(e -> e.getKey().equals(system))
			.findFirst();

		return new SystemReconReport(systemIntelEntry.isPresent(),
				systemIntelEntry.map(Entry::getValue)
					.flatMap(si -> si.colonyIntel().map(ci -> Map.entry(si, ci)))
					.map(siCiEntry -> new ColonyReconReport(siCiEntry.getValue().player(), siCiEntry.getValue().race(),
							siCiEntry.getValue().population(),
							!siCiEntry.getKey().lastSeen().equals(round.orElse(null)))));
	}

}
