package com.scheible.risingempire.util.jdk.function;

/**
 *
 * @author sj
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {

	T get() throws E;
}
