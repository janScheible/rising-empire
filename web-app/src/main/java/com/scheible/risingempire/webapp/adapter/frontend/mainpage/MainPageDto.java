package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.webapp.adapter.frontend.dto.TurnFinishedStatusPlayerDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.ScannerRangeDto;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;
import com.scheible.risingempire.webapp.partial.FieldUtils;
import com.scheible.risingempire.webapp.partial.FieldUtils.Viewport;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
class MainPageDto {

	static class ButtonBarDto {

	}

	static class TurnStatusDto {

		final boolean ownTurnFinished;

		final List<TurnFinishedStatusPlayerDto> playerStatus;

		TurnStatusDto(final boolean ownTurnFinished, final List<TurnFinishedStatusPlayerDto> turnStatus) {
			this.ownTurnFinished = ownTurnFinished;
			this.playerStatus = unmodifiableList(turnStatus);
		}

	}

	static class StateDescriptionDto {

		final String stateName;

		StateDescriptionDto(final String stateName) {
			this.stateName = stateName;
		}

	}

	@JsonProperty(value = "@type")
	final String type = getClass().getSimpleName();

	EntityModel<StarMapDto> starMap;

	InspectorDto inspector = new InspectorDto();

	final boolean fleetMovements;

	final int round;

	final TurnStatusDto turnStatus;

	final StateDescriptionDto stateDescription;

	EntityModel<ButtonBarDto> buttonBar;

	List<String> fields;

	MainPageDto(final boolean fleetMovements, final int round, final TurnStatusDto turnStatus, final StarMapDto starMap,
			final StateDescriptionDto stateDescription) {
		this.fleetMovements = fleetMovements;
		this.round = round;
		this.turnStatus = turnStatus;
		this.starMap = new EntityModel<>(starMap);
		this.stateDescription = stateDescription;
	}

	@SuppressWarnings("checkstyle:CyclomaticComplexity")
	static EntityModel<MainPageDto> filterFields(final EntityModel<MainPageDto> mainPageModel,
			final Optional<String> optionalFields) {
		if (optionalFields.isPresent()) {
			final MainPageDto mainPage = mainPageModel.getContent();

			mainPage.fields = Arrays.asList(optionalFields.get().split(","));
			final Predicate<String> anyFieldStartsWith = fieldStart -> mainPage.fields.stream()
				.anyMatch(f -> f.startsWith(fieldStart));

			mainPage.buttonBar = anyFieldStartsWith.test("$.buttonBar") ? mainPage.buttonBar : null;

			mainPage.inspector = anyFieldStartsWith.test("$.inspector") ? mainPage.inspector : null;

			mainPage.starMap = anyFieldStartsWith.test("$.starMap") ? mainPage.starMap : null;
			if (mainPage.starMap != null) {
				if (anyFieldStartsWith.test("$.starMap.stars")) {
					final Optional<Viewport> viewport = FieldUtils.getViewport(mainPage.fields, "stars");

					mainPage.starMap.getContent().stars = mainPage.starMap.getContent().stars.stream()
						.filter(s -> viewport.isEmpty() || viewport.get().contains(s.getContent().x, s.getContent().y))
						.collect(Collectors.toList());
					mainPage.starMap.getContent().ranges.fleetRanges = mainPage.starMap.getContent().ranges.fleetRanges
						.stream()
						.filter(fr -> viewport.isEmpty()
								|| viewport.get().intersects(fr.centerX, fr.centerY, fr.extendedRadius))
						.collect(Collectors.toList());
					mainPage.starMap.getContent().ranges.colonyScannerRanges = filterScannerRanges(
							mainPage.starMap.getContent().ranges.colonyScannerRanges, viewport);
				}
				else {
					mainPage.starMap.getContent().stars = null;
					mainPage.starMap.getContent().ranges.fleetRanges = null;
					mainPage.starMap.getContent().ranges.colonyScannerRanges = null;
				}

				if (anyFieldStartsWith.test("$.starMap.fleets")) {
					final Optional<Viewport> viewport = FieldUtils.getViewport(mainPage.fields, "fleets");

					mainPage.starMap.getContent().fleets = mainPage.starMap.getContent().fleets.stream()
						.filter(f -> viewport.isEmpty() || viewport.get().contains(f.getContent().x, f.getContent().y))
						.collect(Collectors.toList());
					mainPage.starMap.getContent().ranges.fleetScannerRanges = filterScannerRanges(
							mainPage.starMap.getContent().ranges.fleetScannerRanges, viewport);
				}
				else {
					mainPage.starMap.getContent().fleets = null;
					mainPage.starMap.getContent().ranges.fleetScannerRanges = null;
				}
			}
		}

		return mainPageModel;
	}

	static List<ScannerRangeDto> filterScannerRanges(final Collection<ScannerRangeDto> scannerRanges,
			final Optional<Viewport> viewport) {
		return scannerRanges.stream()
			.filter(csr -> viewport.isEmpty() || viewport.get().intersects(csr.centerX, csr.centerY, csr.radius))
			.collect(Collectors.toList());
	}

}
