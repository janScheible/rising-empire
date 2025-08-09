package com.scheible.risingempire.game.impl2.technology;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.impl2.apiinternal.ResearchPoint;
import com.scheible.risingempire.game.impl2.technology.Technology.SelectTechnology;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class TechnologyTest {

	@Test
	void testSomeMethod() {
		AtomicReference<ResearchPoint> researchPoints = new AtomicReference<>(new ResearchPoint(0));

		Technology technology = new Technology(player -> researchPoints.get(), Set.of(Player.BLUE), 1.0, 1.0);
		assertThat(technology.selectableTechnologies(Player.BLUE)).isEmpty();

		technology.advanceResearch(List.of());
		assertThat(technology.selectableTechnologies(Player.BLUE)).isEmpty();

		researchPoints.set(new ResearchPoint(40));
		// 40 RPs, no technology selected yet -> select one
		technology.advanceResearch(List.of());
		assertThat(technology.selectableTechnologies(Player.BLUE).orElseThrow().next()).extracting(Tech::id)
			.containsOnly(new TechId("FACTORY@1"), new TechId("SHIP@1"), new TechId("RESEARCH@1"));

		// 80RPs
		technology.advanceResearch(List.of(new SelectTechnology(Player.BLUE, new TechId("SHIP@1"))));
		assertThat(technology.selectableTechnologies(Player.BLUE)).isEmpty();

		// 120 RPs --> technology researched -> select next one
		technology.advanceResearch(List.of());
		assertThat(technology.selectableTechnologies(Player.BLUE).orElseThrow().next()).extracting(Tech::id)
			.containsOnly(new TechId("FACTORY@1"), new TechId("SHIP@2"), new TechId("RESEARCH@1"));
	}

}