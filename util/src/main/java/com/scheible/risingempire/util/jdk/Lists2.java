package com.scheible.risingempire.util.jdk;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class Lists2 {

	@SuppressFBWarnings(value = "PREDICTABLE_RANDOM", justification = "Should be random enough.")
	public static <T> T getRandomElement(final List<T> list) {
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}
}
