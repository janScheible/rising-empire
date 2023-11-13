package com.scheible.risingempire.util.jdk;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author sj
 */
public class Optional2 {

	public static <T> boolean ifPresent(Optional<T> optional, Consumer<? super T> action) {
		optional.ifPresent(action);
		return optional.isPresent();
	}

}
