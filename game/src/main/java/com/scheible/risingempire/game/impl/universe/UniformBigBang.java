package com.scheible.risingempire.game.impl.universe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.view.universe.Location;

/**
 * Uniform system distribution created with
 * <a href="https://en.wikipedia.org/wiki/Supersampling#Poisson_disk"> Poisson disk
 * sampling</a>.
 *
 * @author sj
 */
class UniformBigBang implements BigBang {

	@Override
	public Set<Location> getSystemLocations(GalaxySize galaxySize, int maxSystemDistance) {
		PoissonDiscSamplingAlogirthm algorithm = PoissonDiscSamplingAlogirthm.create(galaxySize.getWidth(),
				galaxySize.getHeight(), maxSystemDistance * 0.85);
		Set<Vector> samplePoints = algorithm.sample();

		return samplePoints.stream().map(v -> new Location((int) v.x, (int) v.y)).collect(Collectors.toSet());
	}

	// Inspired by:
	// https://dev.to/christiankastner/poisson-disc-sampling-and-generative-art-2fpd
	private static class PoissonDiscSamplingAlogirthm {

		private static final int K = 30;

		private final double r;

		private final double w;

		private final int cols;

		private final int rows;

		private final Vector[] grid;

		private final List<Vector> active;

		private PoissonDiscSamplingAlogirthm(double r, double w, int cols, int rows, Vector[] grid,
				List<Vector> active) {
			this.r = r;
			this.w = w;

			this.cols = cols;
			this.rows = rows;
			this.grid = grid;

			this.active = active;
		}

		private static PoissonDiscSamplingAlogirthm create(int width, int height, double r) {
			List<Vector> active = new ArrayList<>();

			double w = r / Math.sqrt(2);

			int cols = (int) Math.floor(width / w);
			int rows = (int) Math.floor(height / w);

			Vector[] grid = new Vector[rows * cols];

			return new PoissonDiscSamplingAlogirthm(r, w, cols, rows, grid, active);
		}

		private Set<Vector> sample() {
			for (int i = 0; i < this.cols * this.rows; i++) {
				this.grid[i] = null;
			}

			int startI = this.cols / 2;
			int startIJ = this.rows / 2;

			Vector start = new Vector(startI * this.w + this.w / 2, startIJ * this.w + this.w / 2);
			this.grid[startI + startIJ * this.cols] = start;
			this.active.add(start);

			while (!this.active.isEmpty()) {
				int i = (int) Math.floor(ThreadLocalRandom.current().nextInt(this.active.size()));
				Vector pos = this.active.get(i);

				for (int j = 0; j < K; j++) {
					Vector sample = Vector.random2d();
					double m = this.r * (1 + ThreadLocalRandom.current().nextDouble());
					sample.setLength(m);
					sample.add(pos);

					if (testSample(sample) == true) {
						this.active.add(sample);
						int x = (int) Math.floor(sample.x / this.w);
						int y = (int) Math.floor(sample.y / this.w);
						this.grid[x + y * this.cols] = sample;
						break;
					}
					else if (j == K - 1) {
						this.active.remove(i);
					}
				}
			}

			Set<Vector> result = new HashSet<>();
			for (Vector vector : this.grid) {
				if (vector != null) {
					result.add(vector);
				}
			}
			return result;
		}

		private boolean testSample(Vector sample) {
			int col = (int) Math.floor(sample.x / this.w);
			int row = (int) Math.floor(sample.y / this.w);

			if (col > 0 && row > 0 && col < this.cols - 1 && row < this.rows - 1
					&& this.grid[col + row * this.cols] == null) {
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						int index = col + i + (row + j) * this.cols;
						Vector neighbor = this.grid[index];

						if (neighbor != null) {
							double d = Vector.distance(sample, neighbor);
							if (d < this.r) {
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

	private static class Vector {

		private double x;

		private double y;

		private Vector(double x, double y) {
			this.x = x;
			this.y = y;
		}

		private Vector setLength(double newLength) {
			double currentLength = getLength();
			this.x /= currentLength * (1 / newLength);
			this.y /= currentLength * (1 / newLength);
			return this;
		}

		private double getLength() {
			return Math.sqrt(this.x * this.x + this.y * this.y);
		}

		private Vector add(Vector other) {
			this.x += other.x;
			this.y += other.y;
			return this;
		}

		private static Vector random2d() {
			double angle = Math.random() * Math.PI * 2;
			return new Vector(Math.cos(angle), Math.sin(angle));
		}

		private static double distance(Vector from, Vector to) {
			double dx = from.x - to.x;
			double dy = from.y - to.y;
			return Math.sqrt(dx * dx + dy * dy);
		}

	}

}
