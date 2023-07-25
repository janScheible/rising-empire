package com.scheible.risingempire.webapp.hypermedia;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;

import com.scheible.risingempire.util.jdk.Objects2;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class CollectionModel<T> extends HypermediaModel<CollectionModel<T>> implements Iterable<T> {

	private final Collection<T> elements;

	public CollectionModel(final Collection<T> elements) {
		this.elements = Collections.unmodifiableCollection(elements);
	}

	@Override
	public CollectionModel<T> with(final Action action) {
		actions.add(action);
		return this;
	}

	@Override
	public CollectionModel<T> with(final boolean predicate, final Supplier<Action> actionSupplier) {
		if (predicate) {
			return with(actionSupplier.get());
		} else {
			return this;
		}
	}

	public Collection<T> getElements() {
		return Collections.unmodifiableCollection(elements);
	}

	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}

	@Override
	public int hashCode() {
		return Objects.hash(actions, elements);
	}

	@Override
	@SuppressFBWarnings(value = "EQ_UNUSUAL", justification = "Object2.equals() is allowed.")
	public boolean equals(final Object obj) {
		return Objects2.equals(this, obj,
				other -> Objects.equals(actions, other.actions) && Objects.equals(elements, other.elements));
	}
}
