package com.scheible.risingempire.util.jdk;

import java.util.Optional;
import java.util.StringJoiner;

/**
 * @author sj
 */
public class ToStringBuilder {

	private final StringJoiner joiner = new StringJoiner(", ");

	private final String type;

	ToStringBuilder(String type) {
		this.type = type;
	}

	public ToStringBuilder add(String key, Object value) {
		return add(key, value, "");
	}

	public ToStringBuilder add(String key, Object value, String quoteChar) {
		boolean isOptional = value instanceof Optional<?>;
		Optional<?> optionalValue = isOptional ? (Optional<?>) value : null;

		if (!isOptional || optionalValue.isPresent()) {
			this.joiner.add(key + "=" + quoteChar + (isOptional ? optionalValue.get() : value) + quoteChar);
		}

		return this;
	}

	public ToStringBuilder nest(ToStringBuilder nested) {
		this.joiner.add(nested.toString());
		return this;
	}

	public ToStringBuilder nest(Optional<ToStringBuilder> nested) {
		if (nested.isPresent()) {
			this.joiner.add(nested.get().toString());
		}
		return this;
	}

	@Override
	public String toString() {
		return this.type + "[" + this.joiner.toString() + "]";
	}

}
