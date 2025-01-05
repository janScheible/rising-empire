package com.scheible.risingempire.game.impl2.intelligence.system;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.impl2.apiinternal.Population;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.intelligence.system.ColonyIntelProvider.ColonyIntel;
import com.scheible.risingempire.game.impl2.intelligence.system.SystemReconReport.ColonyReconReport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class SystemIntelligenceTest {

	@Test
	void testJustExploredNoOwnColony() {
		ProviderData providerData = new ProviderData();
		Round round = new Round(1);
		SystemIntelligence intelligence = new SystemIntelligence(providerData.orbitingFleetsProvider(),
				providerData.colonyIntelProvider());

		providerData.orbitingFleets.put(Player.BLUE, Set.of(new Position(5.0, 5.0)));
		intelligence.recon(round);

		round = round.next();
		assertThat(intelligence.justExplored(Player.BLUE, round.previous(), new Position(5.0, 5.0))).isTrue();

		intelligence.recon(round);

		round = round.next();
		assertThat(intelligence.justExplored(Player.BLUE, round.previous(), new Position(5.0, 5.0))).isFalse();
	}

	@Test
	void testJustExploredOwnColony() {
		ProviderData providerData = new ProviderData();
		Round round = new Round(1);
		SystemIntelligence intelligence = new SystemIntelligence(providerData.orbitingFleetsProvider(),
				providerData.colonyIntelProvider());

		providerData.orbitingFleets.put(Player.BLUE, Set.of(new Position(5.0, 5.0)));
		providerData.colonyIntel = new ColonyIntel(Player.BLUE, Race.MYXALOR, new Population(50));
		intelligence.recon(round);

		round = round.next();
		assertThat(intelligence.justExplored(Player.BLUE, round.previous(), new Position(5.0, 5.0))).isFalse();
	}

	@Test
	void testSystemReconReport() {
		ProviderData providerData = new ProviderData();

		Round round = new Round(1);
		SystemIntelligence intelligence = new SystemIntelligence(providerData.orbitingFleetsProvider(),
				providerData.colonyIntelProvider());

		providerData.orbitingFleets.put(Player.BLUE, Set.of(new Position(5.0, 5.0)));
		providerData.colonyIntel = new ColonyIntel(Player.GREEN, Race.MYXALOR, new Population(42));
		intelligence.recon(round);

		round = round.next();

		// unknown system with out an orbiting fleet
		SystemReconReport report = intelligence.systemReconReport(Player.BLUE, round.previous(),
				new Position(3.0, 3.0));
		assertThat(report.explored()).isFalse();
		assertThat(report.colonyReconReport()).isEmpty();

		// colonized system with an orbiting fleet
		report = intelligence.systemReconReport(Player.BLUE, round.previous(), new Position(5.0, 5.0));
		assertThat(report.explored()).isTrue();
		assertThat(report.colonyReconReport())
			.contains(new ColonyReconReport(Player.GREEN, Race.MYXALOR, new Population(42), false));

		// in next round the system with the previous orbiting fleet is outdated
		providerData.orbitingFleets.clear();
		intelligence.recon(round);
		round = round.next();
		assertThat(intelligence.systemReconReport(Player.BLUE, round.previous(), new Position(5.0, 5.0))
			.colonyReconReport()
			.get()
			.outdated()).isTrue();
	}

	private class ProviderData {

		private ColonyIntel colonyIntel = null;

		private final Map<Player, Set<Position>> orbitingFleets = new HashMap<>();

		private OrbitingFleetsProvider orbitingFleetsProvider() {
			return () -> this.orbitingFleets;
		}

		private ColonyIntelProvider colonyIntelProvider() {
			return (Position _) -> Optional.ofNullable(this.colonyIntel);
		}

	}

}