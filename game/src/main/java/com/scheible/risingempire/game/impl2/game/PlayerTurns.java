package com.scheible.risingempire.game.impl2.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.common.Command;

/**
 * @author sj
 */
class PlayerTurns {

	private final Map<Player, PlayerTurn> turnMapping;

	private final Map<Round, Map<Player, List<Command>>> pastCommandsMapping = new HashMap<>();

	private final Set<Player> autoTurn = new HashSet<>();

	PlayerTurns(Set<Player> players) {
		this.turnMapping = new HashMap<>(
				players.stream().collect(Collectors.toMap(Function.identity(), _ -> new PlayerTurn())));
	}

	void beginNewRound(Round round) {
		round.previous()
			.ifPresent(previousRound -> this.pastCommandsMapping.put(previousRound,
					this.turnMapping.entrySet()
						.stream()
						.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().commands()))));

		this.turnMapping.values().stream().forEach(PlayerTurn::beginNewTurn);
	}

	void addCommand(Player player, Command command) {
		this.turnMapping.get(player).addCommand(command);
	}

	<T extends Command> List<T> removeCommands(Player player, Predicate<T> predicate, Class<T> clazz) {
		return this.turnMapping.get(player).removeCommands(predicate, clazz);
	}

	<T extends Command> List<T> commands(Class<T> clazz) {
		return this.turnMapping.values()
			.stream()
			.map(PlayerTurn::commands)
			.flatMap(Collection::stream)
			.filter(clazz::isInstance)
			.map(clazz::cast)
			.toList();
	}

	void finishTurn(Player player) {
		this.turnMapping.get(player).finishTurn();
		this.autoTurn.forEach(p -> this.turnMapping.get(p).finishTurn());
	}

	boolean roundFinished() {
		return this.turnMapping.values().stream().allMatch(PlayerTurn::turnFinished);
	}

	Map<Player, Boolean> turnStatus() {
		return this.turnMapping.entrySet()
			.stream()
			.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().turnFinished()));
	}

	Map<Player, List<Command>> commandMapping() {
		return this.turnMapping.entrySet()
			.stream()
			.collect(Collectors.toMap(Entry::getKey, e -> new ArrayList<>(e.getValue().commands())));
	}

	Map<Round, Map<Player, List<Command>>> pastCommandMapping() {
		return new HashMap<>(this.pastCommandsMapping);
	}

	void enableAutoTurn(Player player) {
		this.autoTurn.add(player);
	}

	void disableAutoTurn(Player player) {
		this.autoTurn.remove(player);
	}

	boolean autoTurn(Player player) {
		return this.autoTurn.contains(player);
	}

	private static class PlayerTurn {

		private List<Command> commands = new ArrayList<>();

		private boolean turnFinished = false;

		private void addCommand(Command command) {
			this.commands.add(command);
		}

		<T extends Command> List<T> removeCommands(Predicate<T> predicate, Class<T> clazz) {
			List<T> removed = new ArrayList<>();
			List<Command> remaining = new ArrayList<>();

			for (int i = 0; i < this.commands.size(); i++) {
				Command command = this.commands.get(i);

				boolean wasRemoved = false;
				if (clazz.isInstance(command)) {
					T castedCommand = clazz.cast(command);
					if (predicate.test(castedCommand)) {
						wasRemoved = true;
						removed.add(castedCommand);
					}
				}

				if (!wasRemoved) {
					remaining.add(command);
				}
			}

			this.commands = remaining;
			return removed;
		}

		private void beginNewTurn() {
			this.commands = new ArrayList<>();
			this.turnFinished = false;
		}

		private void finishTurn() {
			this.turnFinished = true;
		}

		private boolean turnFinished() {
			return this.turnFinished;
		}

		private List<Command> commands() {
			return this.commands;
		}

	}

}
