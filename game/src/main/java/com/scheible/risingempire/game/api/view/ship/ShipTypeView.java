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

	public ShipTypeView(final ShipTypeId id, final int index, final String name, final ShipSize size, final int look) {
		this.id = id;

		this.index = index;
		this.name = name;
		this.size = size;
		this.look = look;
	}

	public ShipTypeId getId() {
		return id;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public ShipSize getSize() {
		return size;
	}

	public int getLook() {
		return look;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			final ShipTypeView other = (ShipTypeView) obj;
			return Objects.equals(id, other.id);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", "ShipType[", "]").add("id=" + id)
			.add("index=" + index)
			.add("name=" + name)
			.add("size=" + size)
			.add("look=" + look)
			.toString();
	}

}
