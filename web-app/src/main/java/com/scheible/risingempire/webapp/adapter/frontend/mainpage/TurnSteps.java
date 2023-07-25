package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.ActionField;

/**
 *
 * @author sj
 */
public class TurnSteps {

	public static ResponseEntity<Void> getMainPageRedirect(final FrontendContext context, final SystemId selectedStarId,
			final Optional<List<String>> spaceCombatSystemId, final Optional<List<String>> exploredSystemId,
			final Optional<List<String>> colonizableSystemId, final Optional<List<String>> notificationSystemId,
			final boolean selectTech) {
		return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION,
						getMainPageAction(context, "not-relevant-for-a-redirect", selectedStarId, spaceCombatSystemId,
								exploredSystemId, colonizableSystemId, notificationSystemId, selectTech).toGetUri())
				.build();
	}

	public static Action getMainPageAction(final FrontendContext context, final String name,
			final SystemId selectedStarId, final Optional<List<String>> spaceCombatSystemId,
			final Optional<List<String>> exploredSystemId, final Optional<List<String>> colonizableSystemId,
			final Optional<List<String>> notificationSystemId, final boolean selectTech) {
		final boolean newTurn = isNewTurn(spaceCombatSystemId, exploredSystemId, colonizableSystemId,
				notificationSystemId, selectTech);

		return context.withSelectedStar(selectedStarId).toNamedAction(name, HttpMethod.GET, true, false, "main-page")
				.with(spaceCombatSystemId.orElseGet(() -> emptyList()).stream()
						.map(esId -> new ActionField("spaceCombatSystemId", esId)))
				.with(exploredSystemId.orElseGet(() -> emptyList()).stream()
						.map(esId -> new ActionField("exploredSystemId", esId)))
				.with(colonizableSystemId.orElseGet(() -> emptyList()).stream()
						.map(csId -> new ActionField("colonizableSystemId", csId)))
				.with(notificationSystemId.orElseGet(() -> emptyList()).stream()
						.map(nsId -> new ActionField("notificationSystemId", nsId)))
				.with(newTurn, "newTurn", () -> Boolean.TRUE);
	}

	private static boolean isNewTurn(final Optional<List<String>> spaceCombatSystemId,
			final Optional<List<String>> exploredSystemId, final Optional<List<String>> colonizableSystemId,
			final Optional<List<String>> notificationSystemId, final boolean selectTech) {
		return spaceCombatSystemId.isEmpty() && exploredSystemId.isEmpty() && colonizableSystemId.isEmpty()
				&& notificationSystemId.isEmpty() && !selectTech;
	}
}
