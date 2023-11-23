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

	public SystemNotificationView(SystemId systemId, Set<String> messages) {
		this.systemId = requireNonNull(systemId);
		this.messages = unmodifiableSet(requireNonNull(messages));
	}

	public SystemId getSystemId() {
		return this.systemId;
	}

	public Set<String> getMessages() {
		return this.messages;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			SystemNotificationView other = (SystemNotificationView) obj;
			return Objects.equals(this.systemId, other.systemId) && Objects.equals(this.messages, other.messages);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.systemId, this.messages);
	}

}
