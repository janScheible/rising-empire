package com.scheible.risingempire.game.api.view.tech;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
public class TechGroupView implements Iterable<TechView> {

	private final Set<TechView> group;

	public TechGroupView(Set<TechView> group) {
		this.group = unmodifiableSet(group);
	}

	@Override
	public Iterator<TechView> iterator() {
		return this.group.iterator();
	}

	public Stream<TechView> stream() {
		return this.group.stream();
	}

}
