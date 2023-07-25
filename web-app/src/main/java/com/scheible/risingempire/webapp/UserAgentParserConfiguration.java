package com.scheible.risingempire.webapp;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;

/**
 *
 * @author sj
 */
@Configuration(proxyBeanMethods = false)
class UserAgentParserConfiguration {

	@Bean
	UserAgentParser userAgentParser() throws IOException, ParseException {
		return new UserAgentService().loadParser();
	}
}
