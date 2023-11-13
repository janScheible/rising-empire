package com.scheible.risingempire.util.jdk.function;

/**
 * @author sj
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {

	R apply(T t) throws E;

}
