package com.scheible.risingempire.web.config.typescript;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author sj
 */
@ConfigurationProperties("application.typescript")
public class TypescriptProperties {
	
	private String path;
	private String tsconfig;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTsconfig() {
		return tsconfig;
	}

	public void setTsconfig(String tsconfig) {
		this.tsconfig = tsconfig;
	}
}
