package com.scheible.risingempire.game.impl2.colonization;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.Population;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.colonization.AnnexedSystemsProvider.AnnexedSystem;
import com.scheible.risingempire.game.impl2.colonization.ArrivingColonistTransportsProvider.ArrivingColonistTransport;
import com.scheible.risingempire.game.impl2.colonization.Colonization.ColonizationCommand;
import com.scheible.risingempire.game.impl2.colonization.Colonization.Colonize;
import com.scheible.risingempire.game.impl2.colonization.Colonization.ColonyCommand;
import com.scheible.risingempire.game.impl2.colonization.Colonization.SpaceDockShipClass;
import com.scheible.risingempire.game.impl2.colonization.Colonization.TransferColonists;
import com.scheible.risingempire.game.impl2.colonization.SpaceDock.ConstructionProgress;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class ColonizationTest {

	private final Population population = new Population(100);

	private final Map<Player, Position> homeSystems = Map.of( //
			Player.BLUE, new Position("6.173", "5.026"), //
			Player.YELLOW, new Position("9.973", "5.626"), //
			Player.WHITE, new Position("4.080", "8.226"));

	@Test
	void testSpaceDockNewShips() {
		ShipClassId first = new ShipClassId("first");
		ShipClassId second = new ShipClassId("second");
		Map<ShipClassId, Credit> shipCosts = Map.of(first, new Credit(2000), second, new Credit(600));

		Position colonySystem = new Position("6.173", "5.026");

		// build capacity per round = 1500 Credits
		Colonization colonization = new Colonization((Player _) -> Set.of(),
				(Player _, ShipClassId shipClassId) -> shipCosts.get(shipClassId), () -> first, () -> Set.of(),
				() -> Set.of(), (_) -> this.population);

		colonization.initialize(this.homeSystems);
		assertSpaceDock(colonization.colony(colonySystem), 0, new Rounds(2),
				new ConstructionProgress(first, new Credit(0)));

		colonization.updateColonies(List.of());
		colonization.buildShips();
		assertThat(colonization.newShips().get(colonySystem)).isNull();
		assertSpaceDock(colonization.colony(colonySystem), 1, new Rounds(1),
				new ConstructionProgress(first, new Credit(1500)));

		colonization.updateColonies(List.of());
		colonization.buildShips();
		assertThat(colonization.newShips().get(colonySystem)).isEqualTo(Map.of(first, 1));
		assertSpaceDock(colonization.colony(colonySystem), 1, new Rounds(1),
				new ConstructionProgress(first, new Credit(1000)));

		assertSpaceDock(colonization.apply(List.of(new SpaceDockShipClass(Player.BLUE, colonySystem, second)))
			.colony(colonySystem), 2, new Rounds(1), new ConstructionProgress(first, new Credit(1000)));
		colonization.updateColonies(List.of(new SpaceDockShipClass(Player.BLUE, colonySystem, second)));
		colonization.buildShips();
		assertThat(colonization.newShips().get(colonySystem)).isEqualTo(Map.of(second, 2));
		assertSpaceDock(colonization.colony(colonySystem), 3, new Rounds(1),
				new ConstructionProgress(second, new Credit(300)));
	}

	private static ObjectAssert<SpaceDock> assertSpaceDock(Optional<Colony> colony, int nextRoundCount, Rounds duration,
			ConstructionProgress constructionProgress) {
		return assertThat(colony.orElseThrow().spaceDock()).satisfies(spaceDock -> {
			assertThat(spaceDock.output().nextRoundCount()).isEqualTo(nextRoundCount);
			assertThat(spaceDock.output().duration()).isEqualTo(duration);
			assertThat(spaceDock.progress()).isEqualTo(constructionProgress);
		});
	}

	@Test
	void testColonize() {
		Position system = new Position("6.000", "8.00");

		Colonization colonization = new Colonization((Player _) -> Set.of(system),
				(Player _, ShipClassId _) -> new Credit(1000), () -> new ShipClassId("first"), () -> Set.of(),
				() -> Set.of(), (_) -> this.population);
		colonization.initialize(this.homeSystems);

		List<ColonizationCommand> commands = List.of(new Colonize(Player.BLUE, system, false));

		assertThat(colonization.apply(commands).colonizeCommand(Player.BLUE, system)).isTrue();

		colonization.updateColonies(
				commands.stream().filter(ColonyCommand.class::isInstance).map(ColonyCommand.class::cast).toList());
		colonization
			.colonizeSystems(commands.stream().filter(Colonize.class::isInstance).map(Colonize.class::cast).toList());

		assertThat(colonization.colony(Player.BLUE, system)).isPresent();
	}

	@Test
	void testAnnexSystems() {
		Position system = new Position("6.173", "5.026");

		Colonization colonization = new Colonization((Player _) -> Set.of(),
				(Player _, ShipClassId _) -> new Credit(1000), () -> new ShipClassId("first"),
				() -> Set.of(new AnnexedSystem(Player.YELLOW, system)), () -> Set.of(), (_) -> this.population);
		colonization.initialize(this.homeSystems);

		assertThat(colonization.colony(system).orElseThrow().player()).isEqualTo(Player.BLUE);

		colonization.annexSystems();

		assertThat(colonization.colony(system).orElseThrow().player()).isEqualTo(Player.YELLOW);
	}

	@Test
	void testCrewColonists() {
		Position system = new Position("6.173", "5.026");

		Colonization colonization = new Colonization((Player _) -> Set.of(),
				(Player _, ShipClassId _) -> new Credit(1000), () -> new ShipClassId("first"), () -> Set.of(),
				() -> Set.of(), (_) -> this.population);
		colonization.initialize(this.homeSystems);

		assertThat(colonization.colony(system).orElseThrow().population()).isEqualTo(new Population(50));

		colonization.crewColonistTransports(
				List.of(new TransferColonists(Player.BLUE, system, new Position(1, 1), new Population(20))));

		assertThat(colonization.colony(system).orElseThrow().population()).isEqualTo(new Population(30));
	}

	@Test
	void testWelcomeColonists() {
		Position system = new Position("6.173", "5.026");

		Colonization colonization = new Colonization((Player _) -> Set.of(),
				(Player _, ShipClassId _) -> new Credit(1000), () -> new ShipClassId("first"), () -> Set.of(),
				() -> Set.of(new ArrivingColonistTransport(Player.BLUE, system, 20)), (_) -> this.population);
		colonization.initialize(this.homeSystems);

		assertThat(colonization.colony(system).orElseThrow().population()).isEqualTo(new Population(50));

		colonization.welcomeColonistTransports();

		assertThat(colonization.colony(system).orElseThrow().population()).isEqualTo(new Population(70));
	}

}