package com.scheible.risingempire.game.api.view;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import static com.scheible.risingempire.game.api.view.fleet.FleetView.FleetViewType.ORBITING;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;

/**
 *
 * @author sj
 */
public class GameView {

	private final int galaxyWidth;
	private final int galaxyHeight;
	private final Player player;
	private final Set<Player> players;
	private final Race race;
	private final int round;
	private final Map<Player, Boolean> turnFinishedStatus;
	private final Map<SystemId, SystemView> systems;
	private final Set<SystemView> systemsSet;
	private final Map<FleetId, FleetView> fleets;
	private final Set<FleetView> fleetsSet;
	private final Set<SystemId> colonizableSystemIds;
	private final Set<SpaceCombatView> spaceCombats;
	private final Set<SystemId> justExploredSystem;
	private final Set<TechGroupView> selectTechGroups;
	private final Set<SystemNotificationView> systemNotifications;

	public GameView(final int galaxyWidth, final int galaxyHeight, final Player player, final Race race,
			final Set<Player> players, final int round, final Map<Player, Boolean> turnFinishedStatus,
			final Set<SystemView> systems, final Set<FleetView> fleets, final Set<SystemId> colonizableSystemIds,
			final Set<SpaceCombatView> spaceCombats, final Set<SystemId> justExploredSystem,
			final Set<TechGroupView> selectTechGroups, final Set<SystemNotificationView> systemNotifications) {
		this.galaxyWidth = galaxyWidth;
		this.galaxyHeight = galaxyHeight;
		this.player = player;
		this.race = race;
		this.players = unmodifiableSet(players);
		this.round = round;
		this.turnFinishedStatus = unmodifiableMap(turnFinishedStatus);
		this.systems = unmodifiableMap(
				systems.stream().collect(Collectors.toMap(SystemView::getId, Function.identity())));
		this.systemsSet = unmodifiableSet(systems);
		this.fleets = unmodifiableMap(fleets.stream().collect(Collectors.toMap(FleetView::getId, Function.identity())));
		this.fleetsSet = unmodifiableSet(fleets);
		this.colonizableSystemIds = unmodifiableSet(colonizableSystemIds);
		this.spaceCombats = unmodifiableSet(spaceCombats);
		this.justExploredSystem = unmodifiableSet(justExploredSystem);
		this.selectTechGroups = unmodifiableSet(selectTechGroups);
		this.systemNotifications = unmodifiableSet(systemNotifications);
	}

	public int getGalaxyWidth() {
		return galaxyWidth;
	}

	public int getGalaxyHeight() {
		return galaxyHeight;
	}

	public Set<TechGroupView> getSelectTechs() {
		return selectTechGroups;
	}

	public Set<SpaceCombatView> getSpaceCombats() {
		return spaceCombats;
	}

	public Player getPlayer() {
		return player;
	}

	public Set<Player> getPlayers() {
		return players;
	}

	public Race getRace() {
		return race;
	}

	public int getRound() {
		return round;
	}

	public boolean isOwnTurnFinished() {
		return turnFinishedStatus.get(player);
	}

	public Map<Player, Boolean> getTurnFinishedStatus() {
		return turnFinishedStatus;
	}

	public Set<SystemView> getSystems() {
		return systemsSet;
	}

	public Set<FleetView> getFleets() {
		return fleetsSet;
	}

	public Set<SystemId> getColonizableSystemIds() {
		return colonizableSystemIds;
	}

	/**
	 * Return all just explored system ids (does not contain the colonizable ones and ones with space battle).
	 */
	public Set<SystemId> getJustExploredSystemIds() {
		return justExploredSystem;
	}

	public Set<SystemNotificationView> getSystemNotifications() {
		return systemNotifications;
	}

	public Optional<FleetView> getOrbiting(final SystemId systemId) {
		return fleetsSet.stream().filter(
				f -> f.getPlayer() == player && f.getType() == ORBITING && f.getOrbiting().get().equals(systemId))
				.findFirst();
	}

	public FleetView getFleet(final FleetId fleetId) {
		return fleets.get(fleetId);
	}

	public SystemView getSystem(final String starName) {
		return systemsSet.stream().filter(s -> s.getStarName().isPresent() && s.getStarName().get().equals(starName))
				.findFirst().get();
	}

	public SystemView getSystem(final SystemId id) {
		return systems.get(id);
	}

	public SystemView getHomeSystem() {
		return systemsSet.stream().filter(SystemView::isHomeSystem).findFirst().get();
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("current turn: ").append(round).append('\n');

		systemsSet.stream().sorted(Comparator.comparing(sv -> getSystemSortPlayer(sv))).map(Object::toString)
				.forEachOrdered(s -> result.append(s).append('\n'));

		fleetsSet.stream()
				.sorted(Comparator.comparing(FleetView::getPlayer)
						.thenComparing(Comparator.comparing(FleetView::getType)))
				.map(fv -> fv.toString(sid -> systems.get(sid).getStarName().orElseGet(() -> sid.toString())))
				.forEachOrdered(f -> result.append(f).append('\n'));

		return result.toString();
	}

	private static String getSystemSortPlayer(final SystemView system) {
		return system.getColonyView().map(ColonyView::getPlayer).map(Object::toString).orElse("");
	}
}
