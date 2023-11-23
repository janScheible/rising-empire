package com.scheible.risingempire.game.api.view;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.fleet.FleetView.FleetViewType;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

/**
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

	private final Set<SystemId> annexableSystemIds;

	private final Set<SpaceCombatView> spaceCombats;

	private final Set<SystemId> justExploredSystem;

	private final Set<TechGroupView> selectTechGroups;

	private final Set<SystemNotificationView> systemNotifications;

	public GameView(int galaxyWidth, int galaxyHeight, Player player, Race race, Set<Player> players, int round,
			Map<Player, Boolean> turnFinishedStatus, Set<SystemView> systems, Set<FleetView> fleets,
			Set<SystemId> colonizableSystemIds, Set<SystemId> annexableSystemIds, Set<SpaceCombatView> spaceCombats,
			Set<SystemId> justExploredSystem, Set<TechGroupView> selectTechGroups,
			Set<SystemNotificationView> systemNotifications) {
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
		this.annexableSystemIds = unmodifiableSet(annexableSystemIds);
		this.spaceCombats = unmodifiableSet(spaceCombats);
		this.justExploredSystem = unmodifiableSet(justExploredSystem);
		this.selectTechGroups = unmodifiableSet(selectTechGroups);
		this.systemNotifications = unmodifiableSet(systemNotifications);
	}

	public int getGalaxyWidth() {
		return this.galaxyWidth;
	}

	public int getGalaxyHeight() {
		return this.galaxyHeight;
	}

	public Set<TechGroupView> getSelectTechs() {
		return this.selectTechGroups;
	}

	public Set<SpaceCombatView> getSpaceCombats() {
		return this.spaceCombats;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Set<Player> getPlayers() {
		return this.players;
	}

	public Race getRace() {
		return this.race;
	}

	public int getRound() {
		return this.round;
	}

	public boolean isOwnTurnFinished() {
		return this.turnFinishedStatus.get(this.player);
	}

	public Map<Player, Boolean> getTurnFinishedStatus() {
		return this.turnFinishedStatus;
	}

	public Set<SystemView> getSystems() {
		return this.systemsSet;
	}

	public Set<FleetView> getFleets() {
		return this.fleetsSet;
	}

	public Set<SystemId> getColonizableSystemIds() {
		return this.colonizableSystemIds;
	}

	public Set<SystemId> getAnnexableSystemIds() {
		return this.annexableSystemIds;
	}

	/**
	 * Return all just explored system ids (does not contain the colonizable ones and ones
	 * with space battle).
	 */
	public Set<SystemId> getJustExploredSystemIds() {
		return this.justExploredSystem;
	}

	public Set<SystemNotificationView> getSystemNotifications() {
		return this.systemNotifications;
	}

	public Optional<FleetView> getOrbiting(SystemId systemId) {
		return this.fleetsSet.stream()
			.filter(f -> f.getPlayer() == this.player && f.getType() == FleetViewType.ORBITING
					&& f.getOrbiting().get().equals(systemId))
			.findFirst();
	}

	public FleetView getFleet(FleetId fleetId) {
		return this.fleets.get(fleetId);
	}

	public SystemView getSystem(String starName) {
		return this.systemsSet.stream()
			.filter(s -> s.getStarName().isPresent() && s.getStarName().get().equals(starName))
			.findFirst()
			.get();
	}

	public SystemView getSystem(SystemId id) {
		return this.systems.get(id);
	}

	public SystemView getHomeSystem() {
		return this.systemsSet.stream().filter(SystemView::isHomeSystem).findFirst().get();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("current turn: ").append(this.round).append('\n');

		this.systemsSet.stream()
			.sorted(Comparator.comparing(sv -> getSystemSortPlayer(sv)))
			.map(Object::toString)
			.forEachOrdered(s -> result.append(s).append('\n'));

		this.fleetsSet.stream()
			.sorted(Comparator.comparing(FleetView::getPlayer).thenComparing(Comparator.comparing(FleetView::getType)))
			.map(fv -> fv.toString(sid -> this.systems.get(sid).getStarName().orElseGet(() -> sid.toString())))
			.forEachOrdered(f -> result.append(f).append('\n'));

		return result.toString();
	}

	private static String getSystemSortPlayer(SystemView system) {
		return system.getColonyView().map(ColonyView::getPlayer).map(Object::toString).orElse("");
	}

}
