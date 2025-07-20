package com.scheible.risingempire.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Custom random class to make sure that it is used in places where a explicit seed is
 * useful.
 *
 * @author sj
 */
public class SeededRandom {

	private final long seed;

	private final Random random;

	public SeededRandom() {
		this.seed = ThreadLocalRandom.current().nextLong();
		this.random = new Random(this.seed);
	}

	public SeededRandom(long seed) {
		this.seed = seed;
		this.random = new Random(this.seed);
	}

	public long seed() {
		return this.seed;
	}

	public int nextInt(int bound) {
		return this.random.nextInt(bound);
	}

	public int nextInt(int origin, int bound) {
		return this.random.nextInt(origin, bound);
	}

	public double nextDouble() {
		return this.random.nextDouble();
	}

	public boolean nextBoolean() {
		return this.random.nextBoolean();
	}

}
