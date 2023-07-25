package com.scheible.risingempire.webapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.scheible.risingempire.webapp.game.GameHolder;

/**
 *
 * @author sj
 */
@Configuration(proxyBeanMethods = false)
class GameHolderConfiguration {

	@Bean
	GameHolder gameHolder() {
		return new GameHolder();
	}
}
