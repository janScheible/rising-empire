package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.webapp.adapter.frontend.dto.TurnFinishedStatusPlayerDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.FleetDto;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
class MainPageDto {

	@JsonProperty(value = "@type")
	final String type = getClass().getSimpleName();

	List<String> parts;

	EntityModel<StarMapDto> starMap;

	InspectorDto inspector = new InspectorDto();

	final int round;

	final TurnStatusDto turnStatus;

	final StateDescriptionDto stateDescription;

	EntityModel<ButtonBarDto> buttonBar;

	List<EntityModel<SpaceCombatDto>> spaceCombats;

	List<EntityModel<ColonizationDto>> colonizations;

	List<EntityModel<ExplorationDto>> explorations;

	List<EntityModel<AnnexationDto>> annexations;

	MainPageDto(int round, TurnStatusDto turnStatus, StarMapDto starMap, StateDescriptionDto stateDescription) {
		this.round = round;
		this.turnStatus = turnStatus;
		this.starMap = new EntityModel<>(starMap);
		this.stateDescription = stateDescription;
	}

	static EntityModel<MainPageDto> filterFields(MainPageState state, EntityModel<MainPageDto> model, boolean partial) {
		if (partial && (state.isStarSpotlightState() || state.isStarInspectionState())) {
			model.getContent().buttonBar = null;
			model.getContent().colonizations = null;
			model.getContent().explorations = null;
			model.getContent().spaceCombats = null;
			model.getContent().starMap.getContent().fleetSelection = null;
			model.getContent().starMap.getContent().fleets = null;
			model.getContent().starMap.getContent().ranges = null;
			model.getContent().starMap.getContent().starBackground = null;
			model.getContent().starMap.getContent().starNotifications = null;
			model.getContent().starMap.getContent().stars = null;

			model.getContent().parts = List.of("$.starMap.starSelection", "$.starMap.fleetSelection", "$.inspector");
		}

		return model;
	}

	static class ButtonBarDto {

	}

	static class TurnStatusDto {

		final boolean ownTurnFinished;

		final List<TurnFinishedStatusPlayerDto> playerStatus;

		TurnStatusDto(boolean ownTurnFinished, List<TurnFinishedStatusPlayerDto> turnStatus) {
			this.ownTurnFinished = ownTurnFinished;
			this.playerStatus = unmodifiableList(turnStatus);
		}

	}

	static class StateDescriptionDto {

		final String stateName;

		StateDescriptionDto(String stateName) {
			this.stateName = stateName;
		}

	}

	static class SpaceCombatDto {

		final List<EntityModel<FleetDto>> destroyedFleets;

		SpaceCombatDto(List<EntityModel<FleetDto>> destroyedFleets) {
			this.destroyedFleets = destroyedFleets;
		}

	}

	static class ColonizationDto {

		final String starId;

		ColonizationDto(String starId) {
			this.starId = starId;
		}

	}

	static class ExplorationDto {

		final String starId;

		ExplorationDto(String starId) {
			this.starId = starId;
		}

	}

	static class AnnexationDto {

		final String starId;

		AnnexationDto(String starId) {
			this.starId = starId;
		}

	}

}
