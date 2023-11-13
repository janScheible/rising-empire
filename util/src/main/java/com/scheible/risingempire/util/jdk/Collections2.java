package com.scheible.risingempire.util.jdk;

import java.util.Collection;

/**
 * @author sj
 */
public class Collections2 {

	/**
	 * Replaces all elements of a collection with the passed new ones.
	 */
	@SafeVarargs
	public static <T> void replaceAll(Collection<T> collection, T... newElements) {
		collection.clear();
		for (T element : newElements) {
			collection.add(element);
		}
	}

}
