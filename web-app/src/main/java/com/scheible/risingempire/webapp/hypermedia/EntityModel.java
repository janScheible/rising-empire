package com.scheible.risingempire.webapp.hypermedia;

import java.util.Objects;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.scheible.risingempire.util.jdk.Objects2;

public class EntityModel<T> extends HypermediaModel<EntityModel<T>> {

	private final T content;

	public EntityModel(T content) {
		this.content = content;
	}

	@Override
	public EntityModel<T> with(Action action) {
		this.actions.add(action);
		return this;
	}

	@Override
	public EntityModel<T> with(boolean predicate, Supplier<Action> actionSupplier) {
		if (predicate) {
			return with(actionSupplier.get());
		}
		else {
			return this;
		}
	}

	@JsonUnwrapped
	public T getContent() {
		return this.content;
	}

	@Override
	public boolean equals(Object obj) {
		return Objects2.equals(this, obj,
				other -> Objects.equals(this.actions, other.actions) && Objects.equals(this.content, other.content));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.actions, this.content);
	}

}
