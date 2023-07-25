package com.scheible.risingempire.webapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.scheible.esbuild.bindings.EsBuild;
import com.scheible.pocketsaw.esbuild.EsBuildMetadata;
import com.scheible.pocketsaw.esbuild.EsBuildMetadata.ParameterBuilder;
import com.scheible.pocketsaw.impl.Pocketsaw;
import com.scheible.pocketsaw.impl.code.PackageDependencies;

/**
 *
 * @author sj
 */
class FrontendSubModulesTest {

	private static final String NUL_DEVICE = new File("/dev/null").exists() ? "/dev/null" : "NUL:";

	private static final Entry<String, Path> ES_BUILD_GAME_BROWSER = Map.entry("./src/game-browser/game-browser.ts",
			Path.of("./target/esbuild-game-browser-metadata.json"));
	private static final Entry<String, Path> ES_BUILD_GAME = Map.entry("./src/bootstrap.ts",
			Path.of("./target/esbuild-game-metadata.json"));
	private static final Entry<String, Path> ES_BUILD_STORYBOOK = Map.entry("./src/storybook/storybook.ts",
			Path.of("./target/esbuild-storybook-metadata.json"));

	private static final Set<Entry<String, String>> ES_BUILD_META_DATA_PARAMETERS = ParameterBuilder
			.rootPackageAlias(new HashSet<>(), "frontend");

	private static Pocketsaw.AnalysisResult result;

	@BeforeAll
	static void beforeClass() throws IOException, InterruptedException {
		for (final Entry<String, Path> esBuildMetadataInvocation : List.of(ES_BUILD_GAME_BROWSER, ES_BUILD_GAME,
				ES_BUILD_STORYBOOK)) {
			EsBuild.run(Path.of("./src/main/frontend"), "--bundle", "--outfile=" + NUL_DEVICE,
					"--metafile=" + esBuildMetadataInvocation.getValue().toAbsolutePath().toString(),
					"--external:~/idb-keyval-6.2.0", "--external:~/fflate-0.7.4", "--external:~/sockette-2.0.6",
					esBuildMetadataInvocation.getKey().toString());
		}

		final EsBuildMetadata esBuildMetaData = new EsBuildMetadata();

		result = Pocketsaw.analize(new File("./src/main/frontend/sub-modules.json"),
				PackageDependencies.merge(
						PackageDependencies.merge(
								esBuildMetaData.read(ES_BUILD_GAME_BROWSER.getValue().toFile(),
										ES_BUILD_META_DATA_PARAMETERS),
								esBuildMetaData.read(ES_BUILD_GAME.getValue().toFile(), ES_BUILD_META_DATA_PARAMETERS)),
						esBuildMetaData.read(ES_BUILD_STORYBOOK.getValue().toFile(), ES_BUILD_META_DATA_PARAMETERS)),
				Optional.of(new File("./target/pocketsaw-frontend-dependency-graph.html")));
	}

	@Test
	void testNoDescriptorCycle() {
		assertThat(result.getAnyDescriptorCycle()).isEmpty();
	}

	@Test
	void testNoCodeCycle() {
		assertThat(result.getAnyCodeCycle()).isEmpty();
	}

	@Test
	void testNoIllegalCodeDependencies() {
		assertThat(result.getIllegalCodeDependencies()).isEmpty();
	}
}
