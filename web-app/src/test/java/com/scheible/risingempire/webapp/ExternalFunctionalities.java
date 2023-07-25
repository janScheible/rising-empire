package com.scheible.risingempire.webapp;

import com.scheible.pocketsaw.api.ExternalFunctionality;

/**
 *
 * @author sj
 */
public class ExternalFunctionalities {

	@ExternalFunctionality(packageMatchPattern = { "com.scheible.risingempire.util.**" })
	public static class RisingEmpireUtil {
	}

	@ExternalFunctionality(packageMatchPattern = { "com.scheible.risingempire.game.**" })
	public static class RisingEmpireGame {
	}

	@ExternalFunctionality(packageMatchPattern = { "com.scheible.esbuild.spring.**" })
	public static class EsBuildSpringDevServer {
	}

	@ExternalFunctionality(packageMatchPattern = { "com.scheible.esbuild.bindings.**" })
	public static class EsBuildBindings {
	}

	@ExternalFunctionality(packageMatchPattern = { "org.springframework.**" })
	public static class SpringFramework {
	}

	@ExternalFunctionality(packageMatchPattern = { "jakarta.servlet.**" })
	public static class JakartaServletApi {
	}

	@ExternalFunctionality(packageMatchPattern = { "org.slf4j.**" })
	public static class Slf4j {
	}

	@ExternalFunctionality(packageMatchPattern = { "com.fasterxml.jackson.**" })
	public static class Jackson {
	}

	@ExternalFunctionality(packageMatchPattern = { "com.blueconic.browscap.**" })
	public static class Browscap {
	}

	/** Is a transient dependency of Spring, shouldn't be used (instead SLF4J)! */
	@ExternalFunctionality(packageMatchPattern = { "org.apache.commons.logging.**" })
	public static class ApacheCommonsLogging {
	}
}
