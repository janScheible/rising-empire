package com.scheible.risingempire.game.api.view.notification;

import java.util.Objects;
import java.util.Set;

import com.scheible.risingempire.game.api.view.system.SystemId;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

/**
 * @author sj
 */
public class SystemNotificationView {

	private final SystemId systemId;

	private final Set<String> messages;

	public SystemNotificationView(final SystemId systemId, final Set<String> messages) {
		this.systemId = requireNonNull(systemId);
		this.messages = unmodifiableSet(requireNonNull(messages));
	}

	public SystemId getSystemId() {
		return systemId;
	}

	public Set<String> getMessages() {
		return messages;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			final SystemNotificationView other = (SystemNotificationView) obj;
			return Objects.equals(systemId, other.systemId) && Objects.equals(messages, other.messages);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(systemId, messages);
	}

}
