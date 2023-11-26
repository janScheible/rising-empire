package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.List;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.ActionField;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author sj
 */
public class TurnSteps {

	public static ResponseEntity<Void> getMainPageRedirect(FrontendContext context, SystemId selectedStarId,
			Optional<List<String>> spaceCombatSystemId, Optional<List<String>> exploredSystemId,
			Optional<List<String>> colonizableSystemId, Optional<List<String>> annexableSystemId,
			Optional<List<String>> notificationSystemId, boolean selectTech) {
		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION,
					getMainPageAction(context, "not-relevant-for-a-redirect", selectedStarId, spaceCombatSystemId,
							exploredSystemId, colonizableSystemId, annexableSystemId, notificationSystemId, selectTech)
						.toGetUri())
			.build();
	}

	public static Action getMainPageAction(FrontendContext context, String name, SystemId selectedStarId,
			Optional<List<String>> spaceCombatSystemId, Optional<List<String>> exploredSystemId,
			Optional<List<String>> colonizableSystemId, Optional<List<String>> annexableSystemId,
			Optional<List<String>> notificationSystemId, boolean selectTech) {
		boolean newTurn = isNewTurn(spaceCombatSystemId, exploredSystemId, colonizableSystemId, annexableSystemId,
				notificationSystemId, selectTech);

		return context.withSelectedStar(selectedStarId)
			.toNamedAction(name, HttpMethod.GET, true, false, "main-page")
			.with(spaceCombatSystemId.orElseGet(() -> List.of())
				.stream()
				.map(esId -> new ActionField("spaceCombatSystemId", esId)))
			.with(exploredSystemId.orElseGet(() -> List.of())
				.stream()
				.map(esId -> new ActionField("exploredSystemId", esId)))
			.with(colonizableSystemId.orElseGet(() -> List.of())
				.stream()
				.map(csId -> new ActionField("colonizableSystemId", csId)))
			.with(annexableSystemId.orElseGet(() -> List.of())
				.stream()
				.map(csId -> new ActionField("annexableSystemId", csId)))
			.with(notificationSystemId.orElseGet(() -> List.of())
				.stream()
				.map(nsId -> new ActionField("notificationSystemId", nsId)))
			.with(newTurn, "newTurn", () -> Boolean.TRUE);
	}

	private static boolean isNewTurn(Optional<List<String>> spaceCombatSystemId,
			Optional<List<String>> exploredSystemId, Optional<List<String>> colonizableSystemId,
			Optional<List<String>> annexableSystemId, Optional<List<String>> notificationSystemId, boolean selectTech) {
		return spaceCombatSystemId.isEmpty() && exploredSystemId.isEmpty() && colonizableSystemId.isEmpty()
				&& annexableSystemId.isEmpty() && notificationSystemId.isEmpty() && !selectTech;
	}

}
