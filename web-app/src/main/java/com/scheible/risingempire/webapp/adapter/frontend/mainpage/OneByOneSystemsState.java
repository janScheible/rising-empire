/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
class OneByOneSystemsState<T> extends MainPageState {

	protected final List<T> systemIds;

	private final SystemId selectedSystemId;

	private final SystemId actualSelectedSystemId;

	OneByOneSystemsState(SystemId selectedSystemId, List<T> systemIds, Function<T, SystemId> systemIdExtractor) {
		this.actualSelectedSystemId = selectedSystemId;
		this.selectedSystemId = systemIdExtractor.apply(systemIds.get(0));
		this.systemIds = systemIds.size() == 1 ? List.of() : unmodifiableList(systemIds.subList(1, systemIds.size()));
	}

	@Override
	Optional<SystemId> getSelectedSystemId() {
		return Optional.of(this.selectedSystemId);
	}

	SystemId getActualSelectedSystemId() {
		return this.actualSelectedSystemId;
	}

	@Override
	boolean isMiniMap() {
		return true;
	}

	@Override
	boolean isSystemSelectable(SystemId systemId) {
		return false;
	}

	@Override
	boolean isFleetSelectable(FleetId fleetId) {
		return false;
	}

}
