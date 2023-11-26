package com.scheible.risingempire.webapp.partial;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import com.scheible.risingempire.util.jdk.Objects2;

/**
 * @author sj
 */
public class FieldUtils {

	private static final Pattern JSON_PATH_BOUNDING_BOX_PATTERN = Pattern.compile(
			"\\$\\.starMap\\.(?<type>\\w+)\\[\\?\\(@\\.x>(?<left>\\d+)&&@\\.x<(?<right>\\d+)&&@\\.y>(?<top>\\d+)&&@\\.y<(?<bottom>\\d+)\\)\\]");

	private static final Pattern ALL_WHITESPACE_PATTERN = Pattern.compile("\\s+");

	public static Optional<Viewport> getViewport(Collection<String> fields, String type) {
		return fields.stream()
			.map(f -> JSON_PATH_BOUNDING_BOX_PATTERN.matcher(ALL_WHITESPACE_PATTERN.matcher(f).replaceAll("")))
			.filter(fm -> fm.matches() && type.equals(fm.group("type")))
			.map(fm -> new Viewport(Integer.parseInt(fm.group("left")), Integer.parseInt(fm.group("right")),
					Integer.parseInt(fm.group("top")), Integer.parseInt(fm.group("bottom"))))
			.findFirst();
	}

	public static class Viewport {

		final int left;

		final int right;

		final int top;

		final int bottom;

		public Viewport(int left, int right, int top, int bottom) {
			this.left = left;
			this.right = right;
			this.top = top;
			this.bottom = bottom;
		}

		public boolean contains(int x, int y) {
			return x >= this.left && x <= this.right && y >= this.top && y <= this.bottom;
		}

		public boolean intersects(int circleX, int circleY, int radius) {
			// temporary variables to set edges for testing
			int testX = circleX;
			int testY = circleY;

			// which edge is closest?
			if (circleX < this.left) { // test left edge
				testX = this.left;
			}
			else if (circleX > this.right) { // right edge
				testX = this.right;
			}

			if (circleY < this.top) { // top edge
				testY = this.top;
			}
			else if (circleY > this.bottom) { // bottom edge
				testY = this.bottom;
			}

			// get distance from closest edges
			int distX = circleX - testX;
			int distY = circleY - testY;

			double distance = Math.sqrt(distX * distX + distY * distY);

			// if the distance is less than the radius, collision!
			return distance <= radius;
		}

		@Override
		public boolean equals(Object obj) {
			return Objects2.equals(this, obj, other -> this.left == other.left && this.right == other.right
					&& this.top == other.top && this.bottom == other.bottom);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.left, this.right, this.top, this.bottom);
		}

	}

}
