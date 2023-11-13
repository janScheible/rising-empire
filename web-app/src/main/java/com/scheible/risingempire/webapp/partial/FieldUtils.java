package com.scheible.risingempire.webapp.partial;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import com.scheible.risingempire.util.jdk.Objects2;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author sj
 */
public class FieldUtils {

	public static class Viewport {

		final int left;

		final int right;

		final int top;

		final int bottom;

		public Viewport(final int left, final int right, final int top, final int bottom) {
			this.left = left;
			this.right = right;
			this.top = top;
			this.bottom = bottom;
		}

		public boolean contains(final int x, final int y) {
			return x >= left && x <= right && y >= top && y <= bottom;
		}

		public boolean intersects(final int circleX, final int circleY, final int radius) {
			// temporary variables to set edges for testing
			int testX = circleX;
			int testY = circleY;

			// which edge is closest?
			if (circleX < left) { // test left edge
				testX = left;
			}
			else if (circleX > right) { // right edge
				testX = right;
			}

			if (circleY < top) { // top edge
				testY = top;
			}
			else if (circleY > bottom) { // bottom edge
				testY = bottom;
			}

			// get distance from closest edges
			final int distX = circleX - testX;
			final int distY = circleY - testY;

			final double distance = Math.sqrt(distX * distX + distY * distY);

			// if the distance is less than the radius, collision!
			return distance <= radius;
		}

		@Override
		@SuppressFBWarnings(value = "EQ_UNUSUAL", justification = "Object2.equals() is allowed.")
		public boolean equals(final Object obj) {
			return Objects2.equals(this, obj,
					other -> left == other.left && right == other.right && top == other.top && bottom == other.bottom);
		}

		@Override
		public int hashCode() {
			return Objects.hash(left, right, top, bottom);
		}

	}

	private static final Pattern JSON_PATH_BOUNDING_BOX_PATTERN = Pattern.compile(
			"\\$\\.starMap\\.(?<type>\\w+)\\[\\?\\(@\\.x>(?<left>\\d+)&&@\\.x<(?<right>\\d+)&&@\\.y>(?<top>\\d+)&&@\\.y<(?<bottom>\\d+)\\)\\]");

	private static final Pattern ALL_WHITESPACE_PATTERN = Pattern.compile("\\s+");

	public static Optional<Viewport> getViewport(final Collection<String> fields, final String type) {
		return fields.stream()
			.map(f -> JSON_PATH_BOUNDING_BOX_PATTERN.matcher(ALL_WHITESPACE_PATTERN.matcher(f).replaceAll("")))
			.filter(fm -> fm.matches() && type.equals(fm.group("type")))
			.map(fm -> new Viewport(Integer.parseInt(fm.group("left")), Integer.parseInt(fm.group("right")),
					Integer.parseInt(fm.group("top")), Integer.parseInt(fm.group("bottom"))))
			.findFirst();
	}

}
