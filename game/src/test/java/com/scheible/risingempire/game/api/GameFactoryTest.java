package com.scheible.risingempire.game.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 * @author sj
 */
@TestMethodOrder(OrderAnnotation.class)
class GameFactoryTest {

	@Test
	@Order(1)
	void testBasicFactoryGet() {
		assertThat(GameFactory.get()).isNotNull();
	}

	@Test
	@Order(2)
	void testFactoryIsSingleton() {
		assertThat(GameFactory.get()).isEqualTo(GameFactory.get());
	}
}
