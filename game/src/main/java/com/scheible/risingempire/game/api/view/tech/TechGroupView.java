package com.scheible.risingempire.game.api.view.tech;

import static java.util.Collections.unmodifiableSet;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author sj
 */
public class TechGroupView implements Iterable<TechView> {

	private final Set<TechView> group;

	public TechGroupView(final Set<TechView> group) {
		this.group = unmodifiableSet(group);
	}

	@Override
	public Iterator<TechView> iterator() {
		return group.iterator();
	}

	public Stream<TechView> stream() {
		return group.stream();
	}
}