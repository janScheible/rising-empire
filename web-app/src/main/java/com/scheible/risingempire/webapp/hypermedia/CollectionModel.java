package com.scheible.risingempire.webapp.hypermedia;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;

import com.scheible.risingempire.util.jdk.Objects2;

public class CollectionModel<T> extends HypermediaModel<CollectionModel<T>> implements Iterable<T> {

	private final Collection<T> elements;

	public CollectionModel(Collection<T> elements) {
		this.elements = Collections.unmodifiableCollection(elements);
	}

	@Override
	public CollectionModel<T> with(Action action) {
		this.actions.add(action);
		return this;
	}

	@Override
	public CollectionModel<T> with(boolean predicate, Supplier<Action> actionSupplier) {
		if (predicate) {
			return with(actionSupplier.get());
		}
		else {
			return this;
		}
	}

	public Collection<T> getElements() {
		return Collections.unmodifiableCollection(this.elements);
	}

	@Override
	public Iterator<T> iterator() {
		return this.elements.iterator();
	}

	@Override
	public boolean equals(Object obj) {
		return Objects2.equals(this, obj,
				other -> Objects.equals(this.actions, other.actions) && Objects.equals(this.elements, other.elements));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.actions, this.elements);
	}

}
