package com.scheible.risingempire.game.api._testgame;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

/**
 * @author sj
 */
public class TestScenarioRunnerExtension extends TypeBasedParameterResolver<TestScenario> {

	@Override
	public TestScenario resolveParameter(ParameterContext pc, ExtensionContext ec) throws ParameterResolutionException {
		return new TestScenarioImpl();
	}

}