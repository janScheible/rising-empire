package com.scheible.risingempire.game;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.modules.syntax.ModuleRuleDefinition.modules;

@AnalyzeClasses(packagesOf = ArchUnitSubModulesTest.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitSubModulesTest {

	@ArchTest
	static final ArchRule noPackageCyclesRule = modules().definedByPackages("com.scheible.risingempire.game.(**)")
		.should()
		.beFreeOfCycles();

}
