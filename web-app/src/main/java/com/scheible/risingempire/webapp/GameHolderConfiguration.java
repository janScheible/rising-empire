package com.scheible.risingempire.webapp;

import com.scheible.risingempire.webapp.game.GameHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sj
 */
@Configuration(proxyBeanMethods = false)
class GameHolderConfiguration {

	@Bean
	GameHolder gameHolder() {
		return new GameHolder();
	}

}
