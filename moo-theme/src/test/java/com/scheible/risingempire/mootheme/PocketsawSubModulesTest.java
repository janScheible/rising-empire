package com.scheible.risingempire.mootheme;

import com.scheible.pocketsaw.impl.Pocketsaw;
import com.scheible.pocketsaw.impl.descriptor.annotation.ClassgraphClasspathScanner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author sj
 */
class PocketsawSubModulesTest {

	@BeforeAll
	static void beforeClass() {
		Pocketsaw
			.analizeClasspath(ClassgraphClasspathScanner.create(PocketsawSubModulesTest.class).enableAutoMatching());
	}

	@Test
	void justMakeItRunAsTestToCreateTheVisualization() {
	}

}
