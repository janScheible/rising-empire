package com.scheible.risingempire.game.api.view.ai;

import com.scheible.risingempire.game.api.ai.AiFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AiFactoryTest {

	@Test
	@Order(1)
	void testBasicFactoryGet() {
		assertThat(AiFactory.get()).isNotNull();
	}

	@Test
	@Order(2)
	void testFactoryIsSingleton() {
		assertThat(AiFactory.get()).isEqualTo(AiFactory.get());
	}

}
