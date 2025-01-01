package com.scheible.risingempire.game.impl2.util;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class LineTest {

	private final Offset<Double> offset = Offset.offset(0.01);

	@Test
	void testIntersectOriginCircleVerticalLineNoIntersection() {
		assertThat(new Line(5.0, 5.0, 5.0, -5.0).intersectOriginCircle(4.0)) //
			.isEmpty();
	}

	@Test
	void testIntersectOriginCircleVerticalLineTangentialIntersection() {
		assertThat(new Line(5.0, 5.0, 5.0, -5.0).intersectOriginCircle(5.0)).satisfiesExactlyInAnyOrder( //
				first -> {
					assertThat(first.x()).isEqualTo(5.0, this.offset);
					assertThat(first.y()).isEqualTo(0.0, this.offset);
				});
	}

	@Test
	void testIntersectOriginCircleVerticalLineIntersection() {
		assertThat(new Line(5.0, 15.0, 5.0, -15.0).intersectOriginCircle(10.0)).satisfiesExactlyInAnyOrder( //
				first -> {
					assertThat(first.x()).isEqualTo(5.0, this.offset);
					assertThat(first.y()).isEqualTo(8.66, this.offset);
				}, second -> {
					assertThat(second.x()).isEqualTo(5.0, this.offset);
					assertThat(second.y()).isEqualTo(-8.66, this.offset);
				});
	}

	@Test
	void testIntersectOriginCircleLineNoIntersection() {
		assertThat(new Line(0.0, 15.0, 1.0, 16.0).intersectOriginCircle(1.0)) //
			.isEmpty();
	}

	@Test
	void testIntersectOriginCircleLineTangentialIntersection() {
		assertThat(new Line(0.0, 6.25, 3.0, 4.0).intersectOriginCircle(5.0)).satisfiesExactlyInAnyOrder( //
				first -> {
					assertThat(first.x()).isEqualTo(3.0, this.offset);
					assertThat(first.y()).isEqualTo(4.0, this.offset);
				});
	}

	@Test
	void testIntersectOriginCircleLineIntersection() {
		assertThat(new Line(0.0, 0.0, 15.0, 15.0).intersectOriginCircle(10.0)).satisfiesExactlyInAnyOrder( //
				first -> {
					assertThat(first.x()).isEqualTo(7.07, this.offset);
					assertThat(first.y()).isEqualTo(7.07, this.offset);
				}, second -> {
					assertThat(second.x()).isEqualTo(-7.07, this.offset);
					assertThat(second.y()).isEqualTo(-7.07, this.offset);
				});
	}

	@Test
	void testIntersectOriginCircleHorizontalLineIntersection() {
		assertThat(new Line(-15.0, 2.0, 15.0, 2.0).intersectOriginCircle(10.0)).satisfiesExactlyInAnyOrder( //
				first -> {
					assertThat(first.x()).isEqualTo(-9.79, this.offset);
					assertThat(first.y()).isEqualTo(2.0, this.offset);
				}, second -> {
					assertThat(second.x()).isEqualTo(9.79, this.offset);
					assertThat(second.y()).isEqualTo(2.0, this.offset);
				});
	}

	@Test
	void testIntersectCircleLineIntersection() {
		assertThat(new Line(0.0, 6.25, 3.0, 4.0).intersectCircle(3.0, 3.0, 5.0)).satisfiesExactlyInAnyOrder( //
				first -> {
					assertThat(first.x()).isEqualTo(-0.46, this.offset);
					assertThat(first.y()).isEqualTo(6.60, this.offset);
				}, second -> {
					assertThat(second.x()).isEqualTo(7.42, this.offset);
					assertThat(second.y()).isEqualTo(0.67, this.offset);
				});
	}

}