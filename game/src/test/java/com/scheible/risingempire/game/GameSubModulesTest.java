package com.scheible.risingempire.game;

import com.scheible.pocketsaw.impl.Pocketsaw;
import com.scheible.pocketsaw.impl.descriptor.annotation.ClassgraphClasspathScanner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class GameSubModulesTest {

	private static Pocketsaw.AnalysisResult result;

	@BeforeAll
	static void beforeClass() {
		result = Pocketsaw.analizeClasspath(ClassgraphClasspathScanner.create(GameSubModulesTest.class));
	}

	@Test
	void testNoCodeCycle() {
		assertThat(result.getAnyCodeCycle()).isEmpty();
	}

}
