package com.scheible.risingempire.game.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for initialized record components that shouldn't be part of the
 * staged builder. Is a bit of a hack because it is used as nullable components. To not
 * leave them with <code>null</code> <code>@RecordBuilder.Initializer</code> has to be
 * used as well for the component marked with that annotation.
 * 
 * @author sj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.PARAMETER })
public @interface Initialized {

}
