package com.scheible.risingempire.game.api.view;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.fleet.FleetView.FleetViewType;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
@StagedRecordBuilder
public record GameView(int galaxyWidth, int galaxyHeight, Player player, Race race, Set<Player> players, int round,
		Map<Player, Boolean> turnFinishedStatus, Map<SystemId, SystemView> systems, Map<FleetId, FleetView> fleets,
		Set<SystemId> colonizableSystemIds, Set<SystemId> annexableSystemIds, Set<SpaceCombatView> spaceCombats,
		Set<SystemId> justExploredSystem, Set<TechGroupView> selectTechGroups,
		Set<SystemNotificationView> systemNotifications, Set<SystemId> colonizationCommandSystemsIds,
		Set<SystemId> annexationCommandSystemsIds) {

	public GameView {
		turnFinishedStatus = unmodifiableMap(turnFinishedStatus);
		systems = unmodifiableMap(systems);
		fleets = unmodifiableMap(fleets);
		colonizableSystemIds = unmodifiableSet(colonizableSystemIds);
		annexableSystemIds = unmodifiableSet(annexableSystemIds);
		spaceCombats = unmodifiableSet(spaceCombats);
		justExploredSystem = unmodifiableSet(justExploredSystem);
		selectTechGroups = unmodifiableSet(selectTechGroups);
		systemNotifications = unmodifiableSet(systemNotifications);
		colonizationCommandSystemsIds = unmodifiableSet(colonizationCommandSystemsIds);
		annexationCommandSystemsIds = unmodifiableSet(annexationCommandSystemsIds);
	}

	/**
	 * Return all just explored system ids (does not contain the colonizable ones and ones
	 * with space battle).
	 */
	public Set<SystemId> justExploredSystemIds() {
		return this.justExploredSystem;
	}

	public Optional<FleetView> orbiting(SystemId systemId) {
		return this.fleets.values()
			.stream()
			.filter(f -> f.player() == this.player && f.type() == FleetViewType.ORBITING
					&& f.orbiting().get().equals(systemId))
			.findFirst();
	}

	public FleetView fleet(FleetId fleetId) {
		return this.fleets.get(fleetId);
	}

	public SystemView system(String starName) {
		return this.systems.values()
			.stream()
			.filter(s -> s.starName().isPresent() && s.starName().get().equals(starName))
			.findFirst()
			.get();
	}

	public SystemView system(SystemId id) {
		return this.systems.get(id);
	}

	public SystemView homeSystem() {
		return this.systems.values().stream().filter(SystemView::homeSystem).findFirst().get();
	}

	public boolean colonizationCommand(SystemId systemId) {
		return this.colonizationCommandSystemsIds.contains(systemId);
	}

	public boolean annexationCommand(SystemId systemId) {
		return this.annexationCommandSystemsIds.contains(systemId);
	}
}
