package com.scheible.risingempire.game.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.soabase.recordbuilder.core.RecordBuilder;
import io.soabase.recordbuilder.core.RecordBuilder.BuilderMode;

/**
 * @author sj
 */
@RecordBuilder.Template(options = @RecordBuilder.Options(builderMode = BuilderMode.STAGED_REQUIRED_ONLY,
		nullablePattern = "^((Initialized))$", addConcreteSettersForOptional = true, emptyDefaultForOptional = false,
		addClassRetainedGenerated = true))
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Inherited
public @interface StagedRecordBuilder {

}
