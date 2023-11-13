package com.scheible.risingempire.util.jdk.function;

import java.util.function.Supplier;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author sj
 */
public class ThrowingFunctions {

	@SuppressFBWarnings(value = "FII_USE_METHOD_REFERENCE",
			justification = "Method reference can't be used because of the exception!")
	public static <T, E extends Exception> Supplier<T> catchException(final ThrowingSupplier<T, E> throwingSupplier) {

		return () -> {
			try {
				return throwingSupplier.get();
			}
			catch (final Exception ex) {
				throw new IllegalStateException(ex);
			}
		};
	}

}
