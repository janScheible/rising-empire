/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.scheible.risingempire.web.config.session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Original source: https://github.com/spring-projects/spring-security/blob/master/samples/chat-jc/src/main/java/sample/config/RedisConfig.java
 * 
 * @author Rob Winch
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 30)
public class RedisConfig {

	@Bean
	public JedisConnectionFactory connectionFactory(RedisConnectionProperties conn) throws Exception {
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setPort(conn.getPort());
		return factory;
	}
}
