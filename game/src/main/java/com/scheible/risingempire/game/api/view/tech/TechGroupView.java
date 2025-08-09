package com.scheible.risingempire.game.api.view.tech;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.view.tech.TechGroupViewBuilder.ResearchedStage;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
@StagedRecordBuilder
public record TechGroupView(TechView researched, List<TechView> next) implements Iterable<TechView> {

	public TechGroupView {
		next = unmodifiableList(next);
	}

	public static ResearchedStage builder() {
		return TechGroupViewBuilder.builder();
	}

	@Override
	public Iterator<TechView> iterator() {
		return this.next.iterator();
	}

	public Stream<TechView> stream() {
		return this.next.stream();
	}

}
