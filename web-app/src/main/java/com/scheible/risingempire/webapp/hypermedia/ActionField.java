package com.scheible.risingempire.webapp.hypermedia;

import java.util.Objects;

import com.scheible.risingempire.util.jdk.Objects2;

/**
 * @author sj
 */
public class ActionField {

	private final String name;

	private final Object value;

	public ActionField(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public Object getValue() {
		return this.value;
	}

	@Override
	public boolean equals(Object obj) {
		return Objects2.equals(this, obj,
				other -> Objects.equals(this.name, other.name) && Objects.equals(this.value, other.value));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.value);
	}

}
