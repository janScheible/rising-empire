package com.scheible.risingempire.game.impl2.util;

import java.util.Comparator;
import java.util.List;

import com.scheible.risingempire.game.impl2.util.Line.Intersection;

/**
 * @author sj
 */
public record LineSegment(Line line) {

	public static LineSegment create(double x0, double y0, double x1, double y1) {
		return new LineSegment(new Line(x0, y0, x1, y1));
	}

	/**
	 * Returns all intersections with the given circle and the line segment. The
	 * intersections are sorted by distance from [x0, y0] (closer first).
	 */
	public List<Intersection> intersectCircle(double x, double y, double r) {
		return this.line.intersectCircle(x, y, r)
			.stream()
			.filter(intersection -> contains(intersection.x(), intersection.y()))
			.sorted(compareTo(this.line.x0(), this.line.y0()))
			.toList();
	}

	public boolean contains(double x, double y) {
		// first double check that the point is on the line at all
		double checkY = this.line().y(x);
		if (!Line.doubleEquals(y, checkY)) {
			throw new IllegalArgumentException("The point [" + x + "," + y + "] is not on the line " + this.line + "!");
		}

		double tx = (x - this.line.x0()) / (this.line.x1() - this.line.x0());
		double ty = (y - this.line.y0()) / (this.line.y1() - this.line.y0());

		return tx >= 0.0 && ty >= 0.0 && tx <= 1.0 && ty <= 1.0;
	}

	private static Comparator<Intersection> compareTo(double x, double y) {
		return (a, b) -> Double.compare(distance(x, y, a.x(), a.y()), distance(x, y, b.x(), b.y()));
	}

	private static double distance(double x0, double y0, double x1, double y1) {
		return Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2));
	}

}
