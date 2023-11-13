package com.scheible.risingempire.util.jdk.function;

import java.util.function.Supplier;

/**
 * @author sj
 */
public class ThrowingFunctions {

	public static <T, E extends Exception> Supplier<T> catchException(ThrowingSupplier<T, E> throwingSupplier) {

		return () -> {
			try {
				return throwingSupplier.get();
			}
			catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
		};
	}

}
