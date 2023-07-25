package com.scheible.risingempire.webapp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.scheible.pocketsaw.impl.Pocketsaw;
import com.scheible.pocketsaw.impl.Pocketsaw.AnalysisResult;
import com.scheible.pocketsaw.impl.descriptor.annotation.ClassgraphClasspathScanner;

/**
 *
 * @author sj
 */
class BackendSubModulesTest {

	private static AnalysisResult result;

	@BeforeAll
	static void beforeClass() {
		result = Pocketsaw.analizeClasspath(ClassgraphClasspathScanner.create(BackendSubModulesTest.class));
	}

	@Test
	void testNoDescriptorCycle() {
		assertThat(result.getAnyDescriptorCycle()).isEmpty();
	}

	@Test
	void testNoCodeCycle() {
		assertThat(result.getAnyCodeCycle()).isEmpty();
	}

	@Test
	void testNoIllegalCodeDependencies() {
		assertThat(result.getIllegalCodeDependencies()).isEmpty();
	}
}
