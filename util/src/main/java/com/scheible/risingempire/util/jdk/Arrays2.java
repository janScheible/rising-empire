package com.scheible.risingempire.util.jdk;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author sj
 */
public class Arrays2 {

	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <T> Set<T> asSet(final T... a) {
		return new HashSet<>(Arrays.asList(a));
	}
}
