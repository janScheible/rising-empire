package com.scheible.risingempire.util.jdk;

import java.util.Optional;
import java.util.StringJoiner;

/**
 * @author sj
 */
public class ToStringBuilder {

	private final StringJoiner joiner = new StringJoiner(", ");

	private final String type;

	ToStringBuilder(final String type) {
		this.type = type;
	}

	public ToStringBuilder add(final String key, final Object value) {
		return add(key, value, "");
	}

	public ToStringBuilder add(final String key, final Object value, final String quoteChar) {
		final boolean isOptional = value instanceof Optional<?>;
		final Optional<?> optionalValue = isOptional ? (Optional<?>) value : null;

		if (!isOptional || optionalValue.isPresent()) {
			joiner.add(key + "=" + quoteChar + (isOptional ? optionalValue.get() : value) + quoteChar);
		}

		return this;
	}

	public ToStringBuilder nest(final ToStringBuilder nested) {
		joiner.add(nested.toString());
		return this;
	}

	public ToStringBuilder nest(final Optional<ToStringBuilder> nested) {
		if (nested.isPresent()) {
			joiner.add(nested.get().toString());
		}
		return this;
	}

	@Override
	public String toString() {
		return type + "[" + joiner.toString() + "]";
	}

}
