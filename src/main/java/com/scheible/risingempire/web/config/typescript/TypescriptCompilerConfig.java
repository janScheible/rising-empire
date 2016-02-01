package com.scheible.risingempire.web.config.typescript;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.jvnet.winp.WinProcess;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Basic idea of running a background task was copied from AbstractRedisInstance.
 * 
 * @author sj
 */
@Configuration
@EnableConfigurationProperties(TypescriptProperties.class)
public class TypescriptCompilerConfig {

	@Bean
	public static TypescriptCompilerConfig.TypescriptCompilerBean typescriptCompiler() {
		return new TypescriptCompilerConfig.TypescriptCompilerBean();
	}

	static class TypescriptCompilerBean implements InitializingBean, DisposableBean {

		@Autowired
		private TypescriptProperties typescriptProperty;

		private Process compilerProcess;
		private volatile boolean active = false;

		@Override
		public void afterPropertiesSet() throws Exception {
			try {
				ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", typescriptProperty.getPath(), "-w");
				processBuilder.directory(new File(new File(".").getCanonicalPath() + typescriptProperty.getTsconfig()));
				compilerProcess = processBuilder.start();
				awaitCompilerReady(compilerProcess);
				active = true;
			} catch (IOException ex) {
				throw new TypescriptCompilerException("Failed to start Typescript compiler.", ex);
			}
		}

		private void awaitCompilerReady(Process compilerProcess) throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(compilerProcess.getInputStream()));
			
			String outputLine;
			do {
				outputLine = reader.readLine();
				if (outputLine == null) {
					throw new TypescriptCompilerException("Can't start Typescript compiler. Check path of 'application.typescript.path' property in application.properties.");
				}
			} while (!outputLine.contains("Watching for file changes."));		
		}

		@Override
		public void destroy() throws Exception {
			if(active) {
				// NOTE Child processes must also be killed (node.exe in that particual case)
				new WinProcess(compilerProcess).killRecursively();

				try {
					compilerProcess.waitFor();
				} catch (InterruptedException ex) {
					throw new TypescriptCompilerException("Failed to stop Typescript compiler.", ex);
				}
			}
		}
	}
}
