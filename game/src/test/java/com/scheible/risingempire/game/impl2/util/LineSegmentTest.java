package com.scheible.risingempire.game.impl2.util;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author sj
 */
class LineSegmentTest {

	private final Offset<Double> offset = Offset.offset(0.01);

	@Test
	void testContains() {
		LineSegment segment = LineSegment.create(0.0, 0.0, 5.0, 5.0);

		assertThat(segment.contains(-1.0, -1.0)).isFalse();

		assertThat(segment.contains(0.0, 0.0)).isTrue();
		assertThat(segment.contains(1.0, 1.0)).isTrue();

		assertThat(segment.contains(6.0, 6.0)).isFalse();
	}

	@Test
	void testContainsPointNotOnLine() {
		LineSegment segment = LineSegment.create(0.0, 0.0, 5.0, 5.0);

		assertThatThrownBy(() -> assertThat(segment.contains(2.0, 2.5)).isTrue())
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testIntersectCircleOnlyOneIntersectionOnSegment() {
		assertThat(LineSegment.create(0.0, 6.25, 8.33, 0.0).intersectCircle(3.0, 3.0, 5.0)).satisfiesExactly( //
				first -> {
					assertThat(first.x()).isEqualTo(7.42, this.offset);
					assertThat(first.y()).isEqualTo(0.67, this.offset);
				});
	}

	@Test
	void testIntersectCircleTwoSortedIntersectionsOnSegment() {
		assertThat(LineSegment.create(-3.0, 8.50, 8.33, 0.0).intersectCircle(3.0, 3.0, 5.0)).satisfiesExactly( //
				first -> {
					assertThat(first.x()).isEqualTo(-0.46, this.offset);
					assertThat(first.y()).isEqualTo(6.60, this.offset);
				}, second -> {
					assertThat(second.x()).isEqualTo(7.42, this.offset);
					assertThat(second.y()).isEqualTo(0.67, this.offset);
				});
	}

}