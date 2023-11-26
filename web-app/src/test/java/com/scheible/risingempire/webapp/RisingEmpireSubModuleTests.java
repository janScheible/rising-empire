package com.scheible.risingempire.webapp;

import java.io.IOException;
import java.util.Optional;

import com.scheible.pocketsaw.impl.Pocketsaw;
import com.scheible.pocketsaw.impl.descriptor.annotation.ClassgraphClasspathScanner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author sj
 */
public class RisingEmpireSubModuleTests {

	@BeforeAll
	static void beforeClass() throws IOException {
		// get the base package of the whole multi-module project
		String basePackage = Optional.of(RisingEmpireSubModuleTests.class)
			.map(clazz -> clazz.getPackage().getName())
			.map(packageName -> packageName.substring(0, packageName.lastIndexOf(".")))
			.get();

		Pocketsaw.analizeClasspath(ClassgraphClasspathScanner.create(basePackage).enableAutoMatching());
	}

	@Test
	void justMakeItRunAsTestToCreateTheVisualization() {
	}

}
