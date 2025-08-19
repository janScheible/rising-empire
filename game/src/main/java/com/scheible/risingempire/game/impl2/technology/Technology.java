package com.scheible.risingempire.game.impl2.technology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.ResearchPoint;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.common.Command;
import com.scheible.risingempire.util.RomanNumberGenerator;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

public class Technology {

	private static final Map<ShipClassId, Speed> SPEEDS = new HashMap<>(Map.of( //
			new ShipClassId("scout"), new Speed(1.5), //
			new ShipClassId("colony-ship"), new Speed(1.0), //
			new ShipClassId("fighter"), new Speed(1.0), //
			new ShipClassId("destroyer"), new Speed(1.0), //
			new ShipClassId("cruiser"), new Speed(1.0), //
			ShipClassId.COLONISTS_TRANSPORTER, new Speed(1.0)));

	private static final Map<ShipClassId, Parsec> RANGES = new HashMap<>(Map.of( //
			new ShipClassId("scout"), new Parsec(4.0), //
			new ShipClassId("colony-ship"), new Parsec(3.0), //
			new ShipClassId("fighter"), new Parsec(3.0), //
			new ShipClassId("destroyer"), new Parsec(3.0), //
			new ShipClassId("cruiser"), new Parsec(3.0), //
			ShipClassId.COLONISTS_TRANSPORTER, new Parsec(3.0)));

	private final ResearchPointProvider researchPointProvider;

	private final Set<Player> players;

	private final Map<ShipClassId, Speed> speeds;

	private final Map<ShipClassId, Parsec> ranges;

	private final Map<Player, Map<TechCategory, Integer>> techCategoryLevels;

	private final Map<Player, Research> currentResearches;

	/** The techs researched in the current round. */
	private final Map<Player, Tech> researchedTechs;

	private final List<SelectTechnology> selectTechnologyCommands;

	public Technology(ResearchPointProvider researchPointProvider, Set<Player> players, double fleetSpeedFactor,
			double fleetRangeFactor) {
		this.researchPointProvider = researchPointProvider;

		this.players = Collections.unmodifiableSet(players);

		this.techCategoryLevels = this.players.stream()
			.collect(Collectors.toMap(Function.identity(),
					p -> Stream.of(TechCategory.values()).collect(Collectors.toMap(Function.identity(), _ -> 0))));

		this.speeds = SPEEDS.entrySet()
			.stream()
			.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().multiply(fleetSpeedFactor)));

		this.ranges = RANGES.entrySet()
			.stream()
			.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().multiply(new Parsec(fleetRangeFactor))));

		this.currentResearches = new HashMap<>();
		this.researchedTechs = new HashMap<>();

		this.selectTechnologyCommands = new ArrayList<>();
	}

	private Technology(ResearchPointProvider researchPointProvider, Set<Player> players, Map<ShipClassId, Speed> speeds,
			Map<ShipClassId, Parsec> ranges, Map<Player, Map<TechCategory, Integer>> techCategoryLevels,
			Map<Player, Research> currentResearches, Map<Player, Tech> researchedTechs,
			List<SelectTechnology> selectTechnologyCommands) {
		this.researchPointProvider = researchPointProvider;

		this.players = unmodifiableSet(players);

		this.speeds = speeds;
		this.ranges = ranges;

		this.techCategoryLevels = unmodifiableMap(techCategoryLevels);
		this.currentResearches = unmodifiableMap(currentResearches);
		this.researchedTechs = unmodifiableMap(researchedTechs);

		this.selectTechnologyCommands = unmodifiableList(selectTechnologyCommands);
	}

	public Technology apply(List<SelectTechnology> commands) {
		Technology copy = new Technology(this.researchPointProvider, this.players, this.speeds, this.ranges,
				this.techCategoryLevels, this.currentResearches, this.researchedTechs, commands);

		return copy;
	}

	public void advanceResearch(List<SelectTechnology> commands) {
		this.researchedTechs.clear();

		for (Player player : this.players) {
			ResearchPoint researchPoints = this.researchPointProvider.researchPoints(player);

			Optional<TechId> commandTechId = commands.stream()
				.filter(c -> c.player().equals(player))
				.findFirst()
				.map(SelectTechnology::techId);

			Research currentResearch = this.currentResearches.get(player);

			if (currentResearch != null && currentResearch.tech().isEmpty()) {
				// In a real game there should be always a command to select the tech.
				// But for automated tests and AI players this is not the case...
				// therfore the fallback for now.
				TechId fallbackTechId = selectableTechnologies(player).orElseThrow().next().getFirst().id();
				TechId techId = commandTechId.orElse(fallbackTechId);

				currentResearch = new Research(player, Optional.of(fromTechId(techId)), currentResearch.progress());
			}

			if (researchPoints.quantity() > 0) {
				if (currentResearch == null) {
					currentResearch = new Research(player, Optional.empty(), new ResearchPoint(0));
				}

				currentResearch = new Research(currentResearch.player(), currentResearch.tech(),
						currentResearch.progress().add(researchPoints));

				boolean doneResarchingCurrentTech = currentResearch.tech().isPresent() && currentResearch.progress()
					.quantity() >= currentResearch.tech().orElseThrow().expense().quantity();

				if (doneResarchingCurrentTech) {
					Tech researchedTech = currentResearch.tech().orElseThrow();
					this.researchedTechs.put(player, researchedTech);
					this.techCategoryLevels.get(player).compute(researchedTech.category(), (key, value) -> value + 1);
					currentResearch = new Research(player, Optional.empty(),
							currentResearch.progress().subtract(researchedTech.expense()));
				}
			}

			this.currentResearches.put(player, currentResearch);
		}
	}

	public Optional<SelectableTech> selectableTechnologies(Player player) {
		boolean selectTechnologyCommand = this.selectTechnologyCommands.stream()
			.anyMatch(c -> c.player().equals(player));

		if (!selectTechnologyCommand && (this.currentResearches.get(player) != null
				&& this.currentResearches.get(player).tech().isEmpty())) {
			return Optional.of(new SelectableTech(Optional.ofNullable(this.researchedTechs.get(player)),
					this.techCategoryLevels.get(player)
						.entrySet()
						.stream()
						.map(e -> fromTechId(new TechId(e.getKey() + "@" + (e.getValue() + 1))))
						.sorted(Comparator.comparing(Tech::name))
						.toList()));
		}
		else {
			return Optional.empty();
		}
	}

	private static Tech fromTechId(TechId id) {
		String[] parts = id.value().split("@");
		TechCategory category = TechCategory.valueOf(parts[0]);
		int level = Integer.parseInt(parts[1]);

		return new Tech(id,
				category.name().substring(0, 1) + category.name().toLowerCase(Locale.ROOT).substring(1) + " "
						+ RomanNumberGenerator.getNumber(level) + " Technology",
				category.description(), level, category, new ResearchPoint(30 * (int) Math.pow(level + 1, 2)));
	}

	public Speed speed(Player player, ShipClassId shipClassId) {
		return this.speeds.get(shipClassId).add(new Speed(this.techCategoryLevels.get(player).get(TechCategory.SHIP)));
	}

	public Parsec range(Player player, ShipClassId shipClassId) {
		return applyTechLevel(player, this.ranges.get(shipClassId));
	}

	public Parsec range(Player player) {
		return applyTechLevel(player, this.ranges.values().stream().min(Parsec::compareTo).get());
	}

	public Parsec extendedRange(Player player) {
		return applyTechLevel(player, this.ranges.values().stream().max(Parsec::compareTo).get());
	}

	private Parsec applyTechLevel(Player player, Parsec baseRange) {
		return baseRange.add(new Parsec(this.techCategoryLevels.get(player).get(TechCategory.SHIP)));
	}

	public Parsec scanRange(Player player, ShipClassId shipClassId) {
		return new Parsec(0.5);
	}

	public Parsec effectiveScanRange(Player player, Set<ShipClassId> shipClassIds) {
		return shipClassIds.stream().map(id -> scanRange(player, id)).max(Parsec::compareTo).orElseThrow();
	}

	public Parsec colonyScanRange(Player player) {
		return new Parsec(1.5);
	}

	public ShipScannerCapability shipScannerCapability(Player player) {
		return ShipScannerCapability.LOCATION_AND_ITINERARY;
	}

	public double factoriesPerPopulation(Player player) {
		return 1.0 + (this.techCategoryLevels.get(player).get(TechCategory.FACTORY)) / 10.0;
	}

	public double researchLabsPerPopulation(Player player) {
		return 0.3 + (this.techCategoryLevels.get(player).get(TechCategory.RESEARCH)) / 4.0;
	}

	public double shipCostTechFactor(Player player) {
		return 1.0 + (this.techCategoryLevels.get(player).get(TechCategory.SHIP)) / 2.0;
	}

	public int shipTechLevel(Player player) {
		return this.techCategoryLevels.get(player).get(TechCategory.SHIP);
	}

	public sealed interface TechnologyCommand extends Command {

	}

	public record AllocateResearch() implements TechnologyCommand {

	}

	public record SelectTechnology(Player player, TechId techId) implements TechnologyCommand {

	}

}
