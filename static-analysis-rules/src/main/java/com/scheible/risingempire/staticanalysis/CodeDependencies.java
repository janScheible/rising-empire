package com.scheible.risingempire.staticanalysis;

import java.util.List;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaModifier.PUBLIC;
import static com.tngtech.archunit.core.domain.JavaModifier.STATIC;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * @author sj
 */
public class CodeDependencies {

	public static final ArchRule noPackageCyclesRule = slices().assignedFrom(new SlicePerPackage())
		.should()
		.beFreeOfCycles();

	public static final ArchRule packageLayeringRule = noClasses().that(not(new MainMethodPredicate()))
		.should(new DependOnDescendantPackagesCondition())
		.allowEmptyShould(true)
		.because("lower packages shouldn't build on higher packages");

	private static class SlicePerPackage implements SliceAssignment {

		@Override
		public SliceIdentifier getIdentifierOf(JavaClass javaClass) {
			return SliceIdentifier.of(javaClass.getPackageName());
		}

		@Override
		public String getDescription() {
			return "Every package is treated as a slice.";
		}

	}

	private static class MainMethodPredicate extends DescribedPredicate<JavaClass> {

		MainMethodPredicate() {
			super("class having a main method");
		}

		@Override
		public boolean test(JavaClass input) {
			return input.getAllMethods()
				.stream()
				.anyMatch(method -> "main".equals(method.getName()) && method.getModifiers().contains(STATIC)
						&& method.getModifiers().contains(PUBLIC) && method.getRawReturnType().getName().equals("void")
						&& method.getRawParameterTypes()
							.stream()
							.map(JavaClass::getName)
							.toList()
							.equals(List.of("[Ljava.lang.String;")));
		}

	}

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

}
