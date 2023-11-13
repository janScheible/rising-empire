package com.scheible.risingempire.util.jdk;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author sj
 */
public class Lists2 {

	public static <T> T getRandomElement(List<T> list) {
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}

}
