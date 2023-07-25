package com.scheible.risingempire.game.api.view.tech;

import java.util.Objects;
import java.util.StringJoiner;

/**
 *
 * @author sj
 */
public class TechView {

	private final TechId id;
	private final String name;
	private final String description;

	public TechView(final TechId id, final String name, final String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public TechId getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj != null && getClass().equals(obj.getClass())) {
			final TechView other = (TechView) obj;
			return Objects.equals(id, other.id) && Objects.equals(name, other.name)
					&& Objects.equals(description, other.description);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		final StringJoiner values = new StringJoiner(", ", "TechView[", "]").add("id=" + id).add("name='" + name + "'")
				.add("description='" + description + "'");
		return values.toString();
	}
}
