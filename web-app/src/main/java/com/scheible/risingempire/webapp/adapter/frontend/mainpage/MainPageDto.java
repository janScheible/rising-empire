package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.webapp.adapter.frontend.dto.PlayerDto;
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

	final PlayerDto playerColor;

	final TurnStatusDto turnStatus;

	final StateDescriptionDto stateDescription;

	EntityModel<ButtonBarDto> buttonBar;

	List<EntityModel<SpaceCombatDto>> spaceCombats;

	List<EntityModel<ColonizationDto>> colonizations;

	List<EntityModel<ExplorationDto>> explorations;

	List<EntityModel<AnnexationDto>> annexations;

	MainPageDto(int round, Player player, TurnStatusDto turnStatus, StarMapDto starMap,
			StateDescriptionDto stateDescription) {
		this.round = round;
		this.playerColor = PlayerDto.fromPlayer(player);
		this.turnStatus = turnStatus;
		this.starMap = new EntityModel<>(starMap);
		this.stateDescription = stateDescription;
	}

	static EntityModel<MainPageDto> filterFields(MainPageState state, EntityModel<MainPageDto> model, String partial) {
		if (!partial.toLowerCase(Locale.ROOT).equals("false")) {
			String[] partialParts = partial.split(",");

			Optional<String> updatedParentFleetId = Stream.of(partialParts)
				.filter(part -> part.startsWith("updatedParentFleetId="))
				.map(part -> part.substring(21))
				.findFirst();

			model.getContent().starMap.getContent().fleets = model.getContent().starMap.getContent().fleets.entrySet()
				.stream()
				.filter(parentIdFleetEntry -> updatedParentFleetId
					.filter(upfid -> upfid.equals(parentIdFleetEntry.getKey()))
					.isPresent())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			model.getContent().starMap.getContent().stars = model.getContent().starMap.getContent().stars.stream()
				.filter(s -> s.getContent().id.equals(state.getSelectedSystemId().map(SystemId::getValue).orElse(null)))
				.toList();
			model.getContent().buttonBar = null;
			model.getContent().colonizations = null;
			model.getContent().explorations = null;
			model.getContent().annexations = null;
			model.getContent().spaceCombats = null;
			model.getContent().starMap.getContent().ranges = null;
			model.getContent().starMap.getContent().starBackground = null;
			model.getContent().starMap.getContent().starNotifications = null;

			model
				.getContent().parts = Stream
					.concat(state.getSelectedSystemId()
						.map(ssid -> "$.starMap.stars[?(@.id=='" + ssid.getValue() + "')]")
						.stream(),
							Stream
								.concat(updatedParentFleetId.map(f -> "$.starMap.fleets." + f).stream(),
										Stream.of("$.starMap.starSelection", "$.starMap.fleetSelection", "$.inspector",
												"$.stateDescription", "$.turnStatus", "$._actions", "$.round")))
					.toList();
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

		ColonizationDto(SystemId starId) {
			this.starId = starId.getValue();
		}

	}

	static class ExplorationDto {

		final String starId;

		ExplorationDto(SystemId starId) {
			this.starId = starId.getValue();
		}

	}

	static class AnnexationDto {

		final String starId;

		AnnexationDto(SystemId starId) {
			this.starId = starId.getValue();
		}

	}

}
