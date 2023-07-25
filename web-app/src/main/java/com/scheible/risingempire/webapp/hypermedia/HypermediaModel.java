package com.scheible.risingempire.webapp.hypermedia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class HypermediaModel<T extends HypermediaModel<? extends T>> {

	protected final List<Action> actions = new ArrayList<>();

	public abstract T with(Action action);

	public abstract T with(boolean predicate, Supplier<Action> actionSupplier);

	@JsonProperty("_actions")
	public List<Action> getActions() {
		return Collections.unmodifiableList(actions);
	}
}
