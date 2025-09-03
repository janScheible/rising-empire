package com.scheible.risingempire.game.impl2.army;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.common.Command;

import static java.util.Collections.unmodifiableMap;

/**
 * @author sj
 */
public class Army {

	private final SiegedSystemsProvider siegedSystemsProvider;

	private final int annexationSiegeRounds;

	private final Map<Player, Map<Position, Rounds>> playerSiegedSystemRounds;

	private final Map<Player, Position> annexedSystems = new HashMap<>();

	private final Set<Annex> annexCommands;

	public Army(SiegedSystemsProvider siegedSystemsProvider, int annexationSiegeRounds) {
		this.playerSiegedSystemRounds = new HashMap<>();

		this.siegedSystemsProvider = siegedSystemsProvider;
		this.annexationSiegeRounds = annexationSiegeRounds;

		this.annexCommands = new HashSet<>();
	}

	private Army(Map<Player, Map<Position, Rounds>> playerSiegedSystemRoundsMapping,
			SiegedSystemsProvider siegedSystemsProvider, int annexationSiegeRounds, Set<Annex> annexCommands) {
		this.playerSiegedSystemRounds = playerSiegedSystemRoundsMapping;

		this.siegedSystemsProvider = siegedSystemsProvider;
		this.annexationSiegeRounds = annexationSiegeRounds;

		this.annexCommands = annexCommands;
	}

	public Army apply(List<ArmyCommand> commands) {
		Set<Annex> currrentAnnexCommands = commands.stream()
			.filter(Annex.class::isInstance)
			.map(Annex.class::cast)
			.collect(Collectors.toSet());

		Army copy = new Army(this.playerSiegedSystemRounds, this.siegedSystemsProvider, this.annexationSiegeRounds,
				currrentAnnexCommands);

		return copy;
	}

	public void annexSystems(Round round, List<Annex> commands) {
		this.annexedSystems.clear();

		Set<SiegedSystem> siegedSystems = this.siegedSystemsProvider.siegedSystems();
		Set<Position> siegedSystemPositions = new HashSet<>();

		for (SiegedSystem siegedSystem : siegedSystems) {
			siegedSystemPositions.add(siegedSystem.position());

			Map<Position, Rounds> systemsSiegeRounds = this.playerSiegedSystemRounds
				.computeIfAbsent(siegedSystem.fleetOwner(), _ -> new HashMap<>());

			Rounds rounds = systemsSiegeRounds.get(siegedSystem.position());

			systemsSiegeRounds.put(siegedSystem.position(),
					rounds != null ? new Rounds(rounds.quantity() + 1) : new Rounds(0));
		}

		// remove all systems that aren't sieged anymore
		for (Entry<Player, Map<Position, Rounds>> playerSiegedSystemRoundsEntry : this.playerSiegedSystemRounds
			.entrySet()) {
			playerSiegedSystemRoundsEntry.getValue().keySet().removeIf(Predicate.not(siegedSystemPositions::contains));
		}

		for (Annex annex : commands) {
			if (annex.skip()) {
				continue;
			}

			if (annexationStatus(annex.player(), annex.system()).map(AnnexationStatus::annexable)
				.orElse(Boolean.FALSE)) {
				this.playerSiegedSystemRounds.get(annex.player()).remove(annex.system());

				this.annexedSystems.put(annex.player(), annex.system());
			}
		}

	}

	public Optional<AnnexationStatus> annexationStatus(Player player, Position system) {
		Rounds rounds = this.playerSiegedSystemRounds.getOrDefault(player, Map.of()).get(system);
		Player siegingPlayer = player;

		// that means player is currently not sieging that system --> check if player is
		// instead being sieged in that system
		if (rounds == null) {
			Optional<SiegedSystem> siegedSystem = this.siegedSystemsProvider.siegedSystems()
				.stream()
				.filter(ss -> ss.position().equals(system) && ss.colonyOwner().equals(player))
				.findFirst();

			if (siegedSystem.isPresent()) {
				siegingPlayer = siegedSystem.get().fleetOwner();
				rounds = this.playerSiegedSystemRounds.getOrDefault(siegingPlayer, Map.of()).get(system);
			}
		}

		if (rounds != null && rounds.quantity() > 0) {
			Rounds siegeRounds = new Rounds(Math.min(this.annexationSiegeRounds, rounds.quantity()));
			Rounds roundsUntilAnnexable = new Rounds(this.annexationSiegeRounds - siegeRounds.quantity());

			boolean annexable = player.equals(siegingPlayer) && rounds.quantity() >= this.annexationSiegeRounds;

			Optional<Annex> annexCommand = this.annexCommands.stream()
				.filter(ac -> !ac.skip() && ac.player() == player && ac.system().equals(system))
				.findFirst();

			return Optional.of(new AnnexationStatus(Optional.of(siegeRounds), Optional.of(roundsUntilAnnexable),
					Optional.of(siegingPlayer), annexable, annexable && annexCommand.isPresent()));
		}
		else {
			return Optional.empty();
		}
	}

	public Map<Player, Position> annexedSystems() {
		return unmodifiableMap(this.annexedSystems);
	}

	public sealed interface ArmyCommand extends Command {

	}

	public record Annex(Player player, Position system, boolean skip) implements ArmyCommand {

	}

}
