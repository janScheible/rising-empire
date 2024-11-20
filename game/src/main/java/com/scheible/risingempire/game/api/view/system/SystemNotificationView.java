package com.scheible.risingempire.game.api.view.system;

import java.util.Set;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.view.system.SystemNotificationViewBuilder.SystemIdStage;

import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
@StagedRecordBuilder
public record SystemNotificationView(SystemId systemId, Set<String> messages) {

	public SystemNotificationView {
		messages = unmodifiableSet(messages);
	}

	public static SystemIdStage builder() {
		return SystemNotificationViewBuilder.builder();
	}

}
