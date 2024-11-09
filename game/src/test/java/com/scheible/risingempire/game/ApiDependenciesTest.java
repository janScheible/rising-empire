package com.scheible.risingempire.game;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * @author sj
 */
@AnalyzeClasses(packagesOf = ApiDependenciesTest.class)
class ApiDependenciesTest {

	@ArchTest
	static final ArchRule apiMustNotDependOnImplRule = noClasses().that()
		.resideInAPackage("..game.api..") //
		.should()
		.dependOnClassesThat()
		.resideInAPackage("..game.impl..");

	@ArchTest
	static final ArchRule apiMustNotDependOnImpl2Rule = noClasses().that()
		.resideInAPackage("..game.api..") //
		.should()
		.dependOnClassesThat()
		.resideInAPackage("..game.impl2..");

	@ArchTest
	static final ArchRule apiMustNotDependOnUtilRule = noClasses().that()
		.resideInAPackage("..game.api..") //
		.should()
		.dependOnClassesThat()
		.resideInAPackage("..game.util..");

}
