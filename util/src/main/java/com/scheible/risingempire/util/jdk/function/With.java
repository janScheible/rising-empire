package com.scheible.risingempire.util.jdk.function;

import java.util.function.Consumer;

/**
 * @author sj
 */
public class With {

	static <T> T with(T obj, Consumer<T> c) {
		c.accept(obj);
		return obj;
	}

}
