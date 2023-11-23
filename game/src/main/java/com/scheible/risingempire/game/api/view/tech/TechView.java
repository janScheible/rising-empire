package com.scheible.risingempire.game.api.view.tech;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author sj
 */
public class TechView {

	private final TechId id;

	private final String name;

	private final String description;

	public TechView(TechId id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public TechId getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			TechView other = (TechView) obj;
			return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name)
					&& Objects.equals(this.description, other.description);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public String toString() {
		StringJoiner values = new StringJoiner(", ", "TechView[", "]").add("id=" + this.id)
			.add("name='" + this.name + "'")
			.add("description='" + this.description + "'");
		return values.toString();
	}

}
