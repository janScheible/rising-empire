package com.scheible.risingempire.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Custom random class to make sure that it is used in places where a explicit seed is
 * usefull.
 *
 * @author sj
 */
public class SeededRandom {

	private final Random random;

	public SeededRandom() {
		this.random = new Random(ThreadLocalRandom.current().nextLong());
	}

	public SeededRandom(long seed) {
		this.random = new Random(seed);
	}

	public int nextInt(int bound) {
		return this.random.nextInt(bound);
	}

	public int nextInt(int origin, int bound) {
		return this.random.nextInt(origin, bound);
	}

}
