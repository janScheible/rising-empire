package com.scheible.risingempire.game.impl2.colonization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.Population;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ResearchPoint;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.colonization.AnnexedSystemsProvider.AnnexedSystem;
import com.scheible.risingempire.game.impl2.colonization.ArrivingColonistTransportsProvider.ArrivingColonistTransport;
import com.scheible.risingempire.game.impl2.colonization.SpaceDock.ConstructionProgress;
import com.scheible.risingempire.game.impl2.colonization.SpaceDock.SpaceDockOutput;
import com.scheible.risingempire.game.impl2.common.Command;
import com.scheible.risingempire.util.Percentage;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
public class Colonization {

	private final List<Colony> colonies;

	private final Map<Player, Position> homeSystems;

	private final ColonyFleetProvider colonyFleetProvider;

	private final ShipCostProvider shipCostProvider;

	private final InitialShipClassProvider initialShipClassProvider;

	private final AnnexedSystemsProvider annexedSystemsProvider;

	private final ArrivingColonistTransportsProvider arrivingColonistTransportsProvider;

	private final MaxPopulationProvider maxPopulationProvider;

	private final FactoryTechProvider factoryTechProvider;

	private final ResearchLabTechProvider researchLabTechProvider;

	private final Map<Position, Entry<Player, Map<ShipClassId, Integer>>> newShips = new HashMap<>();

	private final Set<Position> newColonies = new HashSet<>();

	private final Map<Position, ColonistTransfer> colonistTransfers = new HashMap<>();

	private final Set<Colonize> colonizationCommands;

	private final Set<TransferColonists> transferColonistsCommands;

	public Colonization(ColonyFleetProvider colonyFleetProvider, ShipCostProvider shipCostProvider,
			InitialShipClassProvider initialShipClassProvider, AnnexedSystemsProvider annexedSystemsProvider,
			ArrivingColonistTransportsProvider arrivingColonistTransportsProvider,
			MaxPopulationProvider maxPopulationProvider, FactoryTechProvider factoryTechProvider,
			ResearchLabTechProvider researchLabTechProvider) {
		this.colonies = new ArrayList<>();
		this.homeSystems = new HashMap<>();

		this.colonyFleetProvider = colonyFleetProvider;
		this.shipCostProvider = shipCostProvider;
		this.initialShipClassProvider = initialShipClassProvider;
		this.annexedSystemsProvider = annexedSystemsProvider;
		this.arrivingColonistTransportsProvider = arrivingColonistTransportsProvider;
		this.maxPopulationProvider = maxPopulationProvider;
		this.factoryTechProvider = factoryTechProvider;
		this.researchLabTechProvider = researchLabTechProvider;

		this.colonizationCommands = new HashSet<>();
		this.transferColonistsCommands = new HashSet<>();
	}

	private Colonization(List<Colony> colonies, Map<Player, Position> homeSystems,
			ColonyFleetProvider colonyFleetProvider, ShipCostProvider shipCostProvider,
			InitialShipClassProvider initialShipClassProvider, AnnexedSystemsProvider annexedSystemsProvider,
			ArrivingColonistTransportsProvider arrivingColonistTransportsProvider,
			MaxPopulationProvider maxPopulationProvider, FactoryTechProvider factoryTechProvider,
			ResearchLabTechProvider researchLabTechProvider, Set<Colonize> colonizationCommands,
			Set<TransferColonists> transferColonistsCommands) {
		this.colonies = colonies;
		this.homeSystems = homeSystems;

		this.colonyFleetProvider = colonyFleetProvider;
		this.shipCostProvider = shipCostProvider;
		this.initialShipClassProvider = initialShipClassProvider;
		this.annexedSystemsProvider = annexedSystemsProvider;
		this.arrivingColonistTransportsProvider = arrivingColonistTransportsProvider;
		this.maxPopulationProvider = maxPopulationProvider;
		this.factoryTechProvider = factoryTechProvider;
		this.researchLabTechProvider = researchLabTechProvider;

		this.colonizationCommands = colonizationCommands;
		this.transferColonistsCommands = transferColonistsCommands;
	}

	public void addColonies(Map<Player, Position> systems) {
		Population population = new Population(50);
		Percentage techPercentage = new Percentage(100);

		this.colonies.addAll(systems.entrySet()
			.stream()
			.map(hs -> new Colony(hs.getKey(), hs.getValue(), SpaceDock.UNINITIALIZED, population, techPercentage,
					researchPoints(hs.getKey(), population, techPercentage)))
			.toList());

		Map<Player, List<Colony>> coloniesPerPlayer = this.colonies.stream()
			.filter(c -> systems.containsKey(c.player()))
			.collect(Collectors.groupingBy(Colony::player));
		coloniesPerPlayer.entrySet()
			.stream()
			.filter(e -> e.getValue().size() == 1)
			.forEach(e -> this.homeSystems.put(e.getKey(), e.getValue().getFirst().position()));

		for (int i = 0; i < this.colonies.size(); i++) {
			Colony colony = this.colonies.get(i);

			ShipClassId initalShipClass = this.initialShipClassProvider.initial();
			ConstructionProgress progress = new ConstructionProgress(initalShipClass, new Credit(0));
			Optional<SpaceDockOutput> output = spaceDockOutput(colony.player(), colony.population(),
					colony.techPercentage(), initalShipClass, progress)
				.spaceDockOutput();

			this.colonies.set(i, colony.withSpaceDock(new SpaceDock(initalShipClass, output, progress)));
		}
	}

	public Colonization apply(List<ColonizationCommand> commands) {
		Set<Colonize> currrentColonizationCommands = commands.stream()
			.filter(Colonize.class::isInstance)
			.map(Colonize.class::cast)
			.collect(Collectors.toSet());
		Set<TransferColonists> currentTransferColonistsCommands = commands.stream()
			.filter(TransferColonists.class::isInstance)
			.map(TransferColonists.class::cast)
			.collect(Collectors.toSet());

		Colonization copy = new Colonization(new ArrayList<>(this.colonies), new HashMap<>(this.homeSystems),
				this.colonyFleetProvider, this.shipCostProvider, this.initialShipClassProvider,
				this.annexedSystemsProvider, this.arrivingColonistTransportsProvider, this.maxPopulationProvider,
				this.factoryTechProvider, this.researchLabTechProvider, currrentColonizationCommands,
				currentTransferColonistsCommands);

		copy.updateColonies(
				commands.stream().filter(ColonyCommand.class::isInstance).map(ColonyCommand.class::cast).toList());

		return copy;
	}

	public boolean colonizable(Player player, Position system) {
		return colony(system).isEmpty() && this.colonyFleetProvider.colonizableSystems(player).contains(system);
	}

	public Set<Colony> colonies(Player player) {
		return this.colonies.stream().filter(c -> c.player().equals(player)).collect(Collectors.toSet());
	}

	public Optional<Colony> colony(Player player, Position system) {
		return colony(system).filter(c -> c.player() == player);
	}

	public Optional<Colony> colony(Position system) {
		return this.colonies.stream().filter(c -> c.position().equals(system)).findFirst();
	}

	public boolean homeSystem(Player player, Position position) {
		return this.homeSystems.get(player).equals(position);
	}

	public void updateColonies(List<ColonyCommand> commands) {
		this.newColonies.clear();

		Map<Position, List<ColonyCommand>> colonyCommandMapping = commands.stream()
			.filter(ColonyCommand.class::isInstance)
			.map(ColonyCommand.class::cast)
			.collect(Collectors.groupingBy(ColonyCommand::colony));

		for (int i = 0; i < this.colonies.size(); i++) {
			Colony colony = this.colonies.get(i);
			List<ColonyCommand> colonyCommands = colonyCommandMapping.getOrDefault(colony.position(), List.of());

			SpaceDock spaceDock = colony.spaceDock();

			ShipClassId spaceDockShipClassId = colonyCommands.stream()
				.filter(SpaceDockShipClass.class::isInstance)
				.map(SpaceDockShipClass.class::cast)
				.findFirst()
				.map(SpaceDockShipClass::shipClassId)
				.orElse(spaceDock.current());

			Percentage techPercentage = colonyCommands.stream()
				.filter(AllocateResources.class::isInstance)
				.map(AllocateResources.class::cast)
				.findFirst()
				.map(AllocateResources::percentage)
				.map(Percentage::new)
				.orElse(colony.techPercentage());

			Optional<SpaceDockOutput> output = spaceDockOutput(colony.player(), colony.population(), techPercentage,
					spaceDockShipClassId, spaceDock.progress())
				.spaceDockOutput();

			this.colonies.set(i,
					colony.withSpaceDock(new SpaceDock(spaceDockShipClassId, output, spaceDock.progress()))
						.withTechPercentage(techPercentage)
						.withResearchPoints(researchPoints(colony.player(), colony.population(), techPercentage)));
		}
	}

	public void growPopulations() {
		for (int i = 0; i < this.colonies.size(); i++) {
			Colony colony = this.colonies.get(i);
			this.colonies.set(i, colony.with(colonyBuilder -> colonyBuilder
				.population(colonyBuilder.population().grow(this.maxPopulationProvider.max(colony.position())))));
		}
	}

	public void buildShips() {
		this.newShips.clear();

		for (int i = 0; i < this.colonies.size(); i++) {
			Colony colony = this.colonies.get(i);
			SpaceDock spaceDock = colony.spaceDock();

			SpaceDockOutputWithRemainingInvest finishRoundOutput = spaceDockOutput(colony.player(), colony.population(),
					colony.techPercentage(), spaceDock.current(), spaceDock.progress());

			ConstructionProgress progress = new ConstructionProgress(spaceDock.current(),
					finishRoundOutput.remainingInvest());

			int newShipsCount = finishRoundOutput.spaceDockOutput().map(SpaceDockOutput::nextRoundCount).orElse(0);
			if (newShipsCount > 0) {
				this.newShips.put(colony.position(),
						Map.entry(colony.player(), Map.of(spaceDock.current(), newShipsCount)));
			}

			Optional<SpaceDockOutput> nextRoundOutput = spaceDockOutput(colony.player(), colony.population(),
					colony.techPercentage(), spaceDock.current(), progress)
				.spaceDockOutput();

			this.colonies.set(i, colony.withSpaceDock(new SpaceDock(spaceDock.current(), nextRoundOutput, progress)));
		}
	}

	private SpaceDockOutputWithRemainingInvest spaceDockOutput(Player player, Population population,
			Percentage techPercentage, ShipClassId current, ConstructionProgress progress) {
		Credit buildCapacity = buildCapacity(player, population, techPercentage);
		Credit invest = progress.build(current, buildCapacity);

		if (buildCapacity.quantity() == 0) {
			return new SpaceDockOutputWithRemainingInvest(Optional.empty(), invest);
		}

		Credit shipCost = this.shipCostProvider.cost(player, current);
		int newShipsCount = invest.integerDivide(shipCost);

		Rounds roundsPerShip = new Rounds(1);
		if (newShipsCount == 0) {
			roundsPerShip = new Rounds(shipCost.subtract(invest).divideRoundUp(buildCapacity) + 1);
		}

		return new SpaceDockOutputWithRemainingInvest(Optional.of(new SpaceDockOutput(roundsPerShip, newShipsCount)),
				invest.modulo(shipCost));
	}

	public void colonizeSystems(List<Colonize> commands) {
		for (Colonize colonize : commands) {
			if (colonize.skip()) {
				continue;
			}

			if (this.colonyFleetProvider.colonizableSystems(colonize.player()).contains(colonize.system())) {
				Population population = new Population(5);
				Percentage techPercentage = new Percentage(100);
				Colony preliminaryColony = new Colony(colonize.player(), colonize.system(), SpaceDock.UNINITIALIZED,
						population, techPercentage, researchPoints(colonize.player(), population, techPercentage));

				ShipClassId initalShipClass = this.initialShipClassProvider.initial();
				ConstructionProgress progress = new ConstructionProgress(initalShipClass, new Credit(0));
				Optional<SpaceDockOutput> output = spaceDockOutput(preliminaryColony.player(),
						preliminaryColony.population(), preliminaryColony.techPercentage(), initalShipClass, progress)
					.spaceDockOutput();

				this.colonies.add(preliminaryColony.withSpaceDock(new SpaceDock(initalShipClass, output, progress)));
				this.newColonies.add(colonize.system());
			}
		}
	}

	public void annexSystems() {
		Map<Position, Player> annexedSystems = this.annexedSystemsProvider.annexedSystems()
			.stream()
			.collect(Collectors.toMap(AnnexedSystem::system, AnnexedSystem::player));

		for (int i = 0; i < this.colonies.size(); i++) {
			Colony colony = this.colonies.get(i);

			Player annexPlayer = annexedSystems.get(colony.position());
			if (annexPlayer != null) {
				Player previousPlayer = colony.player();
				this.colonies.set(i, colony.withPlayer(annexPlayer));

				if (this.homeSystem(previousPlayer, colony.position())) {
					Optional<Colony> newHomeSystem = colonies(previousPlayer).stream()
						.sorted(Comparator.comparing(Colony::population)
							.reversed()
							.thenComparing(Colony::position, Comparator.comparing(Position::length)))
						.findFirst();
					newHomeSystem.ifPresent(nhs -> this.homeSystems.put(previousPlayer, nhs.position()));
				}
			}
		}
	}

	public void crewColonistTransports(List<TransferColonists> commands) {
		this.colonistTransfers.clear();

		Map<Position, TransferColonists> colonyCommandMapping = commands.stream()
			.collect(Collectors.toMap(TransferColonists::originColony, Function.identity()));

		for (int i = 0; i < this.colonies.size(); i++) {
			Colony colony = this.colonies.get(i);
			TransferColonists command = colonyCommandMapping.get(colony.position());

			if (command != null && colony.player() == command.player()) {
				this.colonies.set(i, colony.with(colonyBuilder -> colonyBuilder
					.population(colonyBuilder.population().subtract(command.population()))));
				this.colonistTransfers.put(command.originColony(),
						new ColonistTransfer(command.destinationColony(), command.population()));
			}
		}
	}

	public void welcomeColonistTransports() {
		Map<Position, List<ArrivingColonistTransport>> arrivingMapping = this.arrivingColonistTransportsProvider
			.colonistTransports()
			.stream()
			.collect(Collectors.groupingBy(ArrivingColonistTransport::destination));

		for (int i = 0; i < this.colonies.size(); i++) {
			Colony colony = this.colonies.get(i);
			Position colonyPosition = colony.position();
			List<ArrivingColonistTransport> arrivingTransporters = arrivingMapping.getOrDefault(colonyPosition,
					List.of());

			for (ArrivingColonistTransport arrivingTransport : arrivingTransporters) {
				if (colony.player() == arrivingTransport.player()) {
					this.colonies.set(i,
							colony.with(colonyBuilder -> colonyBuilder.population(colonyBuilder.population()
								.add(new Population(arrivingTransport.transporters()),
										this.maxPopulationProvider.max(colonyPosition)))));
					colony = this.colonies.get(i);
				}
			}
		}
	}

	public boolean transfareable(Player player, Position system, Population population) {
		double maxTranfser = Math.floor(colony(player, system).orElseThrow().population().quantity() / 2.0);
		return population.quantity() <= maxTranfser;
	}

	private Credit buildCapacity(Player player, Population population, Percentage techPercentage) {
		if (techPercentage.value() == 100) {
			return new Credit(0);
		}
		else {
			return new Credit((int) (((100 - techPercentage.value()) / 100.0) * population.quantity()
					* this.factoryTechProvider.factoriesPerPopulation(player)));
		}
	}

	private ResearchPoint researchPoints(Player player, Population population, Percentage techPercentage) {
		return new ResearchPoint((int) ((techPercentage.value() / 100.0) * population.quantity()
				* this.researchLabTechProvider.researchLabsPerPopulation(player)));
	}

	public Credit buildCapacity(Player player, Position system) {
		Colony colony = colony(player, system).orElseThrow();
		return buildCapacity(player, colony.population(), colony.techPercentage());
	}

	public ResearchPoint researchPoints(Player player) {
		return colonies(player).stream()
			.map(colony -> researchPoints(player, colony.population(), colony.techPercentage()))
			.reduce(new ResearchPoint(0), ResearchPoint::add);
	}

	public Map<Position, Map<ShipClassId, Integer>> newShips(Player player) {
		return this.newShips.entrySet()
			.stream()
			.filter(e -> e.getValue().getKey() == player)
			.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getValue()));
	}

	public boolean colonizeCommand(Player player, Position system) {
		return this.colonizationCommands.stream()
			.filter(cc -> !cc.skip && cc.player() == player && cc.system().equals(system))
			.findFirst()
			.isPresent();
	}

	public Optional<ColonistTransfer> colonistTransfer(Position system) {
		return this.transferColonistsCommands.stream()
			.filter(tcc -> tcc.originColony.equals(system))
			.findFirst()
			.map(e -> new ColonistTransfer(e.destinationColony(), e.population()));
	}

	public Map<Position, ColonistTransfer> colonistTransfers() {
		return unmodifiableMap(this.colonistTransfers);
	}

	public Set<Position> newColonies() {
		return unmodifiableSet(this.newColonies);
	}

	public sealed interface ColonizationCommand extends Command {

	}

	public record Colonize(Player player, Position system, boolean skip) implements ColonizationCommand {

	}

	public sealed interface ColonyCommand extends ColonizationCommand {

		Position colony();

	}

	public record SpaceDockShipClass(Player player, Position colony, ShipClassId shipClassId) implements ColonyCommand {

	}

	public record AllocateResources(Player player, Position colony, ProductionArea area,
			int percentage) implements ColonyCommand {

	}

	public record TransferColonists(Player player, Position originColony, Position destinationColony,
			Population population) implements ColonyCommand {

		@Override
		public Position colony() {
			return this.originColony;
		}

	}

	private record SpaceDockOutputWithRemainingInvest(Optional<SpaceDockOutput> spaceDockOutput,
			Credit remainingInvest) {

	}

}
