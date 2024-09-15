package com.scheible.risingempire.game.api.view.tech;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;

import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
@StagedRecordBuilder
public record TechGroupView(Set<TechView> group) implements Iterable<TechView> {

	public TechGroupView {
		group = unmodifiableSet(group);
	}

	@Override
	public Iterator<TechView> iterator() {
		return this.group.iterator();
	}

	public Stream<TechView> stream() {
		return this.group.stream();
	}

}
