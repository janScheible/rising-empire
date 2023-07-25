package com.scheible.risingempire.webapp.hypermedia;

import java.util.Objects;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.scheible.risingempire.util.jdk.Objects2;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class EntityModel<T> extends HypermediaModel<EntityModel<T>> {

	private final T content;

	public EntityModel(final T content) {
		this.content = content;
	}

	@Override
	public EntityModel<T> with(final Action action) {
		actions.add(action);
		return this;
	}

	@Override
	public EntityModel<T> with(final boolean predicate, final Supplier<Action> actionSupplier) {
		if (predicate) {
			return with(actionSupplier.get());
		} else {
			return this;
		}
	}

	@JsonUnwrapped
	public T getContent() {
		return content;
	}

	@Override
	public int hashCode() {
		return Objects.hash(actions, content);
	}

	@Override
	@SuppressFBWarnings(value = "EQ_UNUSUAL", justification = "Object2.equals() is allowed.")
	public boolean equals(final Object obj) {
		return Objects2.equals(this, obj,
				other -> Objects.equals(actions, other.actions) && Objects.equals(content, other.content));
	}
}
