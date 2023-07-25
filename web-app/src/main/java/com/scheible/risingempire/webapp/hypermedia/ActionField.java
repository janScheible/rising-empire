package com.scheible.risingempire.webapp.hypermedia;

import java.util.Objects;

import com.scheible.risingempire.util.jdk.Objects2;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class ActionField {

	private final String name;
	private final Object value;

	public ActionField(final String name, final Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value);
	}

	@Override
	@SuppressFBWarnings(value = "EQ_UNUSUAL", justification = "Object2.equals() is allowed.")
	public boolean equals(final Object obj) {
		return Objects2.equals(this, obj,
				other -> Objects.equals(name, other.name) && Objects.equals(value, other.value));
	}
}
