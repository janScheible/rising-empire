package com.scheible.risingempire.web.config.session;

import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

/**
 *
 * @author sj
 */
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 60 * 30)
public class SpringSessionConfig {
    
}
