package com.scheible.risingempire.util.jdk;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * @author sj
 */
public class Objects2 {

	public static <T> boolean equals(T self, Object obj, Function<T, Boolean> equalsFunction) {
		return equals(self, obj, obj != null ? obj.getClass() : null, equalsFunction);
	}

	public static <T> boolean equals(T self, Object obj, Class<?> equalClass, Function<T, Boolean> equalsFunction) {
		if (requireNonNull(self) == obj) {
			return true;
		}
		else if (obj != null && self.getClass().equals(equalClass)) {
			@SuppressWarnings("unchecked")
			T other = (T) obj;
			return equalsFunction.apply(other);
		}
		else {
			return false;
		}
	}

	public static String requireNonEmpty(String value) {
		if (requireNonNull(value).isEmpty()) {
			throw new IllegalArgumentException("String value is not allowed to be empty!");
		}
		return value;
	}

	public static ToStringBuilder toStringBuilder(String type) {
		return new ToStringBuilder(type);
	}

	public static ToStringBuilder toStringBuilder(Class<?> clazz) {
		return new ToStringBuilder(clazz.getSimpleName());
	}

}
