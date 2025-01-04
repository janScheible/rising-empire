package com.scheible.risingempire.game.impl2.technology;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.common.Command;

public class Technology {

	private static final Map<ShipClassId, Speed> SPEEDS = Map.of( //
			new ShipClassId("enterprise"), new Speed(1.0), //
			new ShipClassId("scout"), new Speed(1.5), //
			ShipClassId.COLONISTS_TRANSPORTER, new Speed(1.0));

	private static final Map<ShipClassId, Parsec> RANGES = Map.of(//
			new ShipClassId("enterprise"), new Parsec(3.0), //
			new ShipClassId("scout"), new Parsec(4.0), //
			ShipClassId.COLONISTS_TRANSPORTER, new Parsec(3.0));

	private final ResearchPointProvider researchPointProvider;

	public Technology(ResearchPointProvider researchPointProvider) {
		this.researchPointProvider = researchPointProvider;
		this.researchPointProvider.hashCode(); // to make PMD happy for now...
	}

	public Speed speed(Player player, ShipClassId shipClassId) {
		return SPEEDS.get(shipClassId);
	}

	public Parsec range(Player player, ShipClassId shipClassId) {
		return RANGES.get(shipClassId);
	}

	public Parsec range(Player player) {
		return RANGES.values().stream().min(Parsec::compareTo).get();
	}

	public Parsec extendedRange(Player player) {
		return RANGES.values().stream().max(Parsec::compareTo).get();
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
