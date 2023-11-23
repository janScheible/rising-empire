package com.scheible.risingempire.game.api.view.ship;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author sj
 */
public class ShipTypeView {

	private final ShipTypeId id;

	private final int index;

	private final String name;

	private final ShipSize size;

	private final int look;

	public ShipTypeView(ShipTypeId id, int index, String name, ShipSize size, int look) {
		this.id = id;

		this.index = index;
		this.name = name;
		this.size = size;
		this.look = look;
	}

	public ShipTypeId getId() {
		return this.id;
	}

	public int getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name;
	}

	public ShipSize getSize() {
		return this.size;
	}

	public int getLook() {
		return this.look;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			ShipTypeView other = (ShipTypeView) obj;
			return Objects.equals(this.id, other.id);
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
		return new StringJoiner(", ", "ShipType[", "]").add("id=" + this.id)
			.add("index=" + this.index)
			.add("name=" + this.name)
			.add("size=" + this.size)
			.add("look=" + this.look)
			.toString();
	}

}
