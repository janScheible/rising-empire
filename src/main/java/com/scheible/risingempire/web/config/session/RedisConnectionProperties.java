package com.scheible.risingempire.web.config.session;

import java.io.IOException;

/**
 * Original source: https://github.com/spring-projects/spring-security/blob/master/samples/chat-jc/src/main/java/sample/config/RedisConnectionProperties.java
 * 
 * @author Rob Winch
 */
public interface RedisConnectionProperties {
	
	int getPort() throws IOException;
}
