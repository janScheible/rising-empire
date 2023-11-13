package com.scheible.risingempire.game;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * @author sj
 */
@AnalyzeClasses(packagesOf = PackageLayeringTest.class, importOptions = DoNotIncludeTests.class)
public class PackageLayeringTest {

	private static class DependOnDescendantPackagesCondition extends ArchCondition<JavaClass> {

		DependOnDescendantPackagesCondition() {
			super("depend on descendant packages");
		}

		@Override
		public void check(JavaClass clazz, ConditionEvents events) {
			for (Dependency dependency : clazz.getDirectDependenciesFromSelf()) {
				boolean dependencyOnDescendantPackage = isDependencyOnDescendantPackage(dependency.getOriginClass(),
						dependency.getTargetClass());
				events.add(new SimpleConditionEvent(dependency, dependencyOnDescendantPackage,
						dependency.getDescription()));
			}
		}

		private boolean isDependencyOnDescendantPackage(JavaClass origin, JavaClass target) {
			String originPackageName = origin.getPackageName();
			String targetSubPackagePrefix = target.getPackageName();
			return targetSubPackagePrefix.contains(originPackageName + ".");
		}

	}

	@ArchTest
	static final ArchRule packageLayeringRule = noClasses().that()
		.resideInAnyPackage("com.scheible.risingempire.game.impl.ship",
				"com.scheible.risingempire.game.impl.spacecombat")
		.should(new DependOnDescendantPackagesCondition())
		.because("lower layers/packages shouldn't build on higher layers/packages");

}
