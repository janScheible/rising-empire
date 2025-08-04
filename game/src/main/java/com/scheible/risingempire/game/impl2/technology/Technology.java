package com.scheible.risingempire.game.impl2.technology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.common.Command;

public class Technology {

	private final Map<ShipClassId, Speed> speeds = new HashMap<>(Map.of( //
			new ShipClassId("scout"), new Speed(1.5), //
			new ShipClassId("colony-ship"), new Speed(1.0), //
			new ShipClassId("fighter"), new Speed(1.0), //
			new ShipClassId("destroyer"), new Speed(1.0), //
			new ShipClassId("cruiser"), new Speed(1.0), //
			ShipClassId.COLONISTS_TRANSPORTER, new Speed(1.0)));

	private final Map<ShipClassId, Parsec> ranges = new HashMap<>(Map.of( //
			new ShipClassId("scout"), new Parsec(4.0), //
			new ShipClassId("colony-ship"), new Parsec(3.0), //
			new ShipClassId("fighter"), new Parsec(3.0), //
			new ShipClassId("destroyer"), new Parsec(3.0), //
			new ShipClassId("cruiser"), new Parsec(3.0), //
			ShipClassId.COLONISTS_TRANSPORTER, new Parsec(3.0)));

	private final ResearchPointProvider researchPointProvider;

	public Technology(ResearchPointProvider researchPointProvider, double fleetSpeedFactor, double fleetRangeFactor) {
		this.researchPointProvider = researchPointProvider;
		this.researchPointProvider.hashCode(); // to make PMD happy for now...

		this.speeds.putAll(this.speeds.entrySet()
			.stream()
			.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().multiply(fleetSpeedFactor))));

		this.ranges.putAll(this.ranges.entrySet()
			.stream()
			.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().multiply(new Parsec(fleetRangeFactor)))));
	}

	public Speed speed(Player player, ShipClassId shipClassId) {
		return this.speeds.get(shipClassId);
	}

	public Parsec range(Player player, ShipClassId shipClassId) {
		return this.ranges.get(shipClassId);
	}

	public Parsec range(Player player) {
		return this.ranges.values().stream().min(Parsec::compareTo).get();
	}

	public Parsec extendedRange(Player player) {
		return this.ranges.values().stream().max(Parsec::compareTo).get();
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

	public void advanceResearch(List<SelectTechnology> commands) {
	}

	public Set<TechId> selectableTechnologies(Player player) {
		return Set.of();
	}

	public ShipScannerCapability shipScannerCapability(Player player) {
		return ShipScannerCapability.LOCATION_AND_ITINERARY;
	}

	public sealed interface TechnologyCommand extends Command {

	}

	public record AllocateResearch() implements TechnologyCommand {

	}

	public record SelectTechnology(Player player, TechId techId) implements TechnologyCommand {

	}

}
