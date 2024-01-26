package com.scheible.risingempire.util.jdk;

import java.util.List;

import com.scheible.risingempire.util.SeededRandom;

/**
 * @author sj
 */
public class Lists2 {

	public static <T> T getRandomElement(List<T> list, SeededRandom random) {
		return list.get(random.nextInt(list.size()));
	}

}
