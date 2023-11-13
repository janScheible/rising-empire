package com.scheible.risingempire.util;

/**
 * Wrapper class for marking an object instance as new or already existing. Can for
 * example be used with <code>Optional#map(...)</code> and
 * <code>Optional#orElseGet(...)</code>.
 *
 * @author sj
 */
public class ProcessingResult<T> {

	private final T value;

	private final boolean existing;

	private ProcessingResult(final T value, final boolean existing) {
		this.value = value;
		this.existing = existing;
	}

	public static <T> ProcessingResult<T> existing(final T value) {
		return new ProcessingResult<>(value, true);
	}

	public static <T> ProcessingResult<T> created(final T value) {
		return new ProcessingResult<>(value, false);
	}

	public T get() {
		return this.value;
	}

	public boolean didExist() {
		return this.existing;
	}

	public boolean wasCreated() {
		return !this.existing;
	}

}
