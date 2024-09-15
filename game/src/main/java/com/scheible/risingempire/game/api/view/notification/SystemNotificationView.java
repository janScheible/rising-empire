package com.scheible.risingempire.game.api.view.notification;

import java.util.Set;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.view.system.SystemId;

import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
@StagedRecordBuilder
public record SystemNotificationView(SystemId systemId, Set<String> messages) {

	public SystemNotificationView {
		messages = unmodifiableSet(messages);
	}

}
