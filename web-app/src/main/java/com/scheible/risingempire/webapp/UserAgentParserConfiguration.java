package com.scheible.risingempire.webapp;

import java.io.IOException;

import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sj
 */
@Configuration(proxyBeanMethods = false)
class UserAgentParserConfiguration {

	@Bean
	UserAgentParser userAgentParser() throws IOException, ParseException {
		return new UserAgentService().loadParser();
	}

}
