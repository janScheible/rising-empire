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
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.common.Order;

/**
 * @author sj
 */
class PlayerTurns {

	private final Map<Player, PlayerTurn> turnMapping;

	private final Map<Round, Map<Player, List<Order>>> pastOrdersMapping = new HashMap<>();

	private final Set<Player> autoTurn = new HashSet<>();

	PlayerTurns(Set<Player> players) {
		this.turnMapping = new HashMap<>(
				players.stream().collect(Collectors.toMap(Function.identity(), _ -> new PlayerTurn())));
	}

	void beginNewRound(Round round) {
		round.previous()
			.ifPresent(previousRound -> this.pastOrdersMapping.put(previousRound,
					this.turnMapping.entrySet()
						.stream()
						.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().orders()))));

		this.turnMapping.values().stream().forEach(PlayerTurn::beginNewTurn);
	}

	void addOrder(Player player, Order order) {
		this.turnMapping.get(player).addOrder(order);
	}

	<T extends Order> List<T> orders(Player player, Class<T> clazz) {
		return this.turnMapping.get(player).orders.stream().filter(clazz::isInstance).map(clazz::cast).toList();
	}

	<T extends Order> List<T> orders(Class<T> clazz) {
		return this.turnMapping.values()
			.stream()
			.map(PlayerTurn::orders)
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

	Map<Round, Map<Player, List<Order>>> pastOrdersMapping() {
		return new HashMap<>(this.pastOrdersMapping);
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

		private List<Order> orders = new ArrayList<>();

		private boolean turnFinished = false;

		private void addOrder(Order order) {
			this.orders.add(order);
		}

		private void beginNewTurn() {
			this.orders = new ArrayList<>();
			this.turnFinished = false;
		}

		private void finishTurn() {
			this.turnFinished = true;
		}

		private boolean turnFinished() {
			return this.turnFinished;
		}

		private List<Order> orders() {
			return this.orders;
		}

	}

}
