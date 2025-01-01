package com.scheible.risingempire.game.impl2.util;

import java.util.List;
import java.util.function.Function;

/**
 * @author sj
 */
public record Line(double x0, double y0, double x1, double y1) {

	private static final double EPSILON = 0.000_001d;

	/**
	 * Slope of the line.
	 */
	public double m() {
		return (this.y1 - this.y0) / (this.x1 - this.x0);
	}

	/**
	 * y-intercept of the line.
	 */
	public double b() {
		return (this.y0 * this.x1 - this.y1 * this.x0) / (this.x1 - this.x0);
	}

	public double y(double x) {
		return m() * x + b();
	}

	/**
	 * Returns all intersections with the given circle and the line.
	 */
	public List<Intersection> intersectCircle(double x, double y, double r) {
		if (doubleEquals(x, 0.0) && doubleEquals(y, 0.0)) {
			return intersectOriginCircle(r);
		}
		else {
			// translate everything so that the circle is at the origin to simplify the
			// calcualtion of the intersections
			return new Line(this.x0 - x, this.y0 - y, this.x1 - x, this.y1 - y).intersectOriginCircle(r)
				.stream()
				.map(intersection -> new Intersection(intersection.x() + x, intersection.y() + y))
				.toList();
		}
	}

	/**
	 * Returns all intersections with a circle located at the origin and the line.
	 */
	List<Intersection> intersectOriginCircle(double r) {
		Function<Double, Double> yCircleFunction = concreteX -> Math.sqrt(Math.pow(r, 2) - Math.pow(concreteX, 2));

		if (doubleEquals(this.x0, this.x1)) {
			double x = this.x0;

			if (doubleEquals(x, r)) {
				return List.of(new Intersection(x, 0.0));
			}
			else if (x > -r && x < r) {
				double y = yCircleFunction.apply(x);

				return List.of(new Intersection(x, y), new Intersection(x, -y));
			}
			else {
				return List.of();
			}
		}
		else {
			double m = this.m();
			double b = this.b();

			// the sqrt radicand of the midnight formula
			double sqrtRadicand = Math.pow(2 * m * b, 2) - 4 * (Math.pow(m, 2) + 1) * (Math.pow(b, 2) - Math.pow(r, 2));
			Function<Double, Double> xFunction = concreteSqrt -> ((-2 * m * b) + concreteSqrt)
					/ (2 * (Math.pow(m, 2) + 1));

			if (doubleEquals(sqrtRadicand, 0.0)) {
				double intersectX = xFunction.apply(0.0);
				double intersectY = y(intersectX);

				return List.of(new Intersection(intersectX, intersectY));
			}
			else if (sqrtRadicand > 0.0) {

				double sqrt = Math.sqrt(sqrtRadicand);
				double intersectX0 = xFunction.apply(sqrt);
				double intersectY0 = y(intersectX0);

				double intersectX1 = xFunction.apply(-sqrt);
				double intersecty1 = y(intersectX1);

				return List.of(new Intersection(intersectX0, intersectY0), new Intersection(intersectX1, intersecty1));
			}
			else {
				return List.of();
			}
		}
	}

	static boolean doubleEquals(double a, double b) {
		return Math.abs(a - b) < EPSILON;
	}

	public record Intersection(double x, double y) {

	}

}