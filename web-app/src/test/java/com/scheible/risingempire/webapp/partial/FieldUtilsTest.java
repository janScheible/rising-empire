package com.scheible.risingempire.webapp.partial;

import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
class FieldUtilsTest {

	@Test
	void testViewportCircleIntersection() {
		assertThat(new FieldUtils.Viewport(80, 240, 60, 160).intersects(250, 150, 50)).isTrue();
		assertThat(new FieldUtils.Viewport(80, 240, 60, 160).intersects(50, 205, 50)).isFalse();
	}

	@Test
	void testViewportParsing() {
		assertThat(FieldUtils.getViewport(
				singletonList("$.starMap.stars[?(@.x > 0 && @.x < 1003 && @.y > 19 && @.y < 500)]"), "stars"))
						.isPresent().hasValue(new FieldUtils.Viewport(0, 1003, 19, 500));
	}

	@Test
	void testWrongType() {
		assertThat(FieldUtils.getViewport(
				singletonList("$.starMap.stars[?(@.x > 0 && @.x < 1003 && @.y > 19 && @.y < 500)]"), "fleets"))
						.isEmpty();
	}
}
