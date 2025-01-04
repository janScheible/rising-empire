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
	void testJustExplored() {
		ProviderData providerData = new ProviderData();
		Round round = new Round(1);
		SystemIntelligence intelligence = new SystemIntelligence(providerData.arrivedFleetsProvider(),
				providerData.colonyIntelProvider());

		providerData.arrivedFleets.put(Player.BLUE, Set.of(new Position(5.0, 5.0)));
		intelligence.recon(round = round.next());
		assertThat(intelligence.justExplored(Player.BLUE, round, new Position(5.0, 5.0))).isTrue();

		intelligence.recon(round = round.next());
		assertThat(intelligence.justExplored(Player.BLUE, round, new Position(5.0, 5.0))).isFalse();
	}

	@Test
	void testSystemReconReport() {
		ProviderData providerData = new ProviderData();

		Round round = new Round(1);
		SystemIntelligence intelligence = new SystemIntelligence(providerData.arrivedFleetsProvider(),
				providerData.colonyIntelProvider());

		providerData.arrivedFleets.put(Player.BLUE, Set.of(new Position(5.0, 5.0)));
		providerData.colonyIntel = new ColonyIntel(Player.GREEN, Race.MYXALOR, new Population(42));
		intelligence.recon(round);

		// unknown system
		SystemReconReport report = intelligence.systemReconReport(Player.BLUE, new Position(3.0, 3.0));
		assertThat(report.explored()).isFalse();
		assertThat(report.colonyReconReport()).isEmpty();

		// system with arrived fleet
		report = intelligence.systemReconReport(Player.BLUE, new Position(5.0, 5.0));
		assertThat(report.explored()).isTrue();
		assertThat(report.colonyReconReport())
			.contains(new ColonyReconReport(Player.GREEN, Race.MYXALOR, new Population(42)));
	}

	private class ProviderData {

		private ColonyIntel colonyIntel = null;

		private final Map<Player, Set<Position>> arrivedFleets = new HashMap<>();

		private ArrivedFleetsProvider arrivedFleetsProvider() {
			return () -> this.arrivedFleets;
		}

		private ColonyIntelProvider colonyIntelProvider() {
			return (Player _, Position _) -> Optional.ofNullable(this.colonyIntel);
		}

	}

}