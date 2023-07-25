package com.scheible.risingempire.game.impl.universe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.view.universe.Location;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Uniform system distribution created with
 * <a href="https://en.wikipedia.org/wiki/Supersampling#Poisson_disk"> Poisson disk sampling</a>.
 * 
 * @author sj
 */
class UniformBigBang implements BigBang {

	private static class Vector {

		private double x;
		private double y;

		private Vector(final double x, final double y) {
			this.x = x;
			this.y = y;
		}

		private Vector setLength(final double newLength) {
			final double currentLength = getLength();
			x /= currentLength * (1 / newLength);
			y /= currentLength * (1 / newLength);
			return this;
		}

		private double getLength() {
			return Math.sqrt(x * x + y * y);
		}

		private Vector add(final Vector other) {
			x += other.x;
			y += other.y;
			return this;
		}

		@SuppressFBWarnings(value = "PREDICTABLE_RANDOM", justification = "Should be good enough.")
		private static Vector random2d() {
			final double angle = Math.random() * Math.PI * 2;
			return new Vector(Math.cos(angle), Math.sin(angle));
		}

		private static double distance(final Vector from, final Vector to) {
			final double dx = from.x - to.x;
			final double dy = from.y - to.y;
			return Math.sqrt(dx * dx + dy * dy);
		}
	}

	// Inspired by: https://dev.to/christiankastner/poisson-disc-sampling-and-generative-art-2fpd
	private static class PoissonDiscSamplingAlogirthm {

		private static final int K = 30;

		private final double r;
		private final double w;

		private final int cols;
		private final int rows;
		private final Vector[] grid;

		private final List<Vector> active;
		private final Random random;

		private PoissonDiscSamplingAlogirthm(final double r, final double w, final int cols, final int rows,
				final Vector[] grid, final List<Vector> active, final Random random) {
			this.r = r;
			this.w = w;

			this.cols = cols;
			this.rows = rows;
			this.grid = grid;

			this.active = active;
			this.random = random;
		}

		@SuppressFBWarnings(value = "PREDICTABLE_RANDOM", justification = "Should be good enough for sampling.")
		private static PoissonDiscSamplingAlogirthm create(final int width, final int height, final double r) {
			final List<Vector> active = new ArrayList<>();
			final Random random = ThreadLocalRandom.current();

			final double w = r / Math.sqrt(2);

			final int cols = (int) Math.floor(width / w);
			final int rows = (int) Math.floor(height / w);

			final Vector[] grid = new Vector[rows * cols];

			return new PoissonDiscSamplingAlogirthm(r, w, cols, rows, grid, active, random);
		}

		private Set<Vector> sample() {
			for (int i = 0; i < cols * rows; i++) {
				grid[i] = null;
			}

			final int startI = cols / 2;
			final int startIJ = rows / 2;

			final Vector start = new Vector(startI * w + w / 2, startIJ * w + w / 2);
			grid[startI + startIJ * cols] = start;
			active.add(start);

			while (!active.isEmpty()) {
				final int i = (int) Math.floor(random.nextInt(active.size()));
				final Vector pos = active.get(i);

				for (int j = 0; j < K; j++) {
					final Vector sample = Vector.random2d();
					final double m = r * (1 + random.nextDouble());
					sample.setLength(m);
					sample.add(pos);

					if (testSample(sample) == true) {
						active.add(sample);
						final int x = (int) Math.floor(sample.x / w);
						final int y = (int) Math.floor(sample.y / w);
						grid[x + y * cols] = sample;
						break;
					} else if (j == K - 1) {
						active.remove(i);
					}
				}
			}

			final Set<Vector> result = new HashSet<>();
			for (final Vector vector : grid) {
				if (vector != null) {
					result.add(vector);
				}
			}
			return result;
		}

		private boolean testSample(final Vector sample) {
			final int col = (int) Math.floor(sample.x / w);
			final int row = (int) Math.floor(sample.y / w);

			if (col > 0 && row > 0 && col < cols - 1 && row < rows - 1 && grid[col + row * cols] == null) {
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						final int index = col + i + (row + j) * cols;
						final Vector neighbor = grid[index];

						if (neighbor != null) {
							final double d = Vector.distance(sample, neighbor);
							if (d < r) {
								return false;
							}
						}
					}
				}

				return true;
			}

			return false;
		}
	}

	@Override
	public Set<Location> getSystemLocations(final GalaxySize galaxySize, final int maxSystemDistance) {
		final PoissonDiscSamplingAlogirthm algorithm = PoissonDiscSamplingAlogirthm.create(galaxySize.getWidth(),
				galaxySize.getHeight(), maxSystemDistance * 0.85);
		final Set<Vector> samplePoints = algorithm.sample();

		return samplePoints.stream().map(v -> new Location((int) v.x, (int) v.y)).collect(Collectors.toSet());
	}
}
