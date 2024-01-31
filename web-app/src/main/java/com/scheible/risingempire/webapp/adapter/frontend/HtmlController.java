package com.scheible.risingempire.webapp.adapter.frontend;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scheible.esbuild.bindings.util.ImportMapGenerator;
import com.scheible.esbuild.bindings.util.ImportMapper;
import com.scheible.esbuild.spring.AppRevision;
import com.scheible.risingempire.game.api.universe.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 * @author sj
 */
@Controller
@RequestMapping(produces = TEXT_HTML_VALUE)
class HtmlController {

	private final AppRevision appRevision;

	private final ImportMapGenerator importMapGenerator;

	private final ServletContext servletContext;

	HtmlController(AppRevision appRevision, ImportMapGenerator importMapGenerator, ServletContext servletContext) {
		this.appRevision = appRevision;
		this.importMapGenerator = importMapGenerator;
		this.servletContext = servletContext;
	}

	@GetMapping(path = "/")
	ModelAndView gameBrowserHtml() {
		return new ModelAndView("gameBrowserView");
	}

	@Bean
	View gameBrowserView() {
		return new TemplateView("/templates/game-browser.html", Arrays.asList( //
				Map.entry("${importMap}", model -> this.importMapGenerator.generate(Map.of( //
						ImportMapper.FRONTEND_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/frontend", //
						ImportMapper.LIBRARY_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/lib", //
						ImportMapper.APP_REVISION_PLACEHOLDER, this.appRevision.value()))),
				Map.entry("${appRevision}", model -> this.appRevision.value())));
	}

	@GetMapping(path = "/games/{gameId}/{player}")
	ModelAndView gameHtml(@PathVariable String gameId, @PathVariable Player player) {
		return new ModelAndView("gameView", Map.of("gameId", gameId, "player", player));
	}

	@Bean
	View gameView() {
		return new TemplateView("/templates/game.html", Arrays.asList( //
				Map.entry("${gameId}", model -> model.get("gameId").toString()),
				Map.entry("${urlEncodedGameId}", model -> urlEncode(model.get("gameId").toString())),
				Map.entry("${player}", model -> model.get("player").toString()),
				Map.entry("${importMap}", model -> this.importMapGenerator.generate(Map.of( //
						ImportMapper.FRONTEND_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/frontend", //
						ImportMapper.LIBRARY_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/lib", //
						ImportMapper.APP_REVISION_PLACEHOLDER, this.appRevision.value()))),
				Map.entry("${appRevision}", model -> this.appRevision.value())));
	}

	private static String urlEncode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	@GetMapping(path = "/storybook.html", produces = TEXT_HTML_VALUE)
	ModelAndView storybookHtml() {
		return new ModelAndView("storybookView");
	}

	@Bean
	View storybookView() {
		return new TemplateView("/templates/storybook.html", Arrays.asList( //
				Map.entry("${importMap}", model -> this.importMapGenerator.generate(Map.of( //
						ImportMapper.FRONTEND_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/frontend", //
						ImportMapper.LIBRARY_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/lib", //
						ImportMapper.APP_REVISION_PLACEHOLDER, this.appRevision.value()))),
				Map.entry("${appRevision}", model -> this.appRevision.value())));
	}

	static class TemplateView implements View {

		private final String templatePath;

		private final Collection<Entry<String, Function<Map<String, ?>, String>>> replacements;

		TemplateView(String templatePath, Collection<Entry<String, Function<Map<String, ?>, String>>> replacements) {
			this.templatePath = templatePath;
			this.replacements = replacements;
		}

		@Override
		public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			if (model != null) {
				response.setContentType(getContentType());

				List<Entry<String, String>> evaluatedReplacement = this.replacements.stream()
					.map(rep -> Map.entry(rep.getKey(), rep.getValue().apply(model)))
					.collect(Collectors.toList());
				String renderedTemplate = render(new ClassPathResource(this.templatePath), evaluatedReplacement);
				response.getWriter().append(renderedTemplate);
			}
			else {
				response.sendError(INTERNAL_SERVER_ERROR.value());
			}
		}

		private String render(Resource templateResource, Collection<Entry<String, String>> replacements)
				throws IOException {
			try (InputStream input = templateResource.getInputStream()) {
				String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);

				for (Entry<String, String> replacement : replacements) {
					template = template.replace(replacement.getKey(), replacement.getValue());
				}

				return template;
			}
		}

		@Override
		public String getContentType() {
			return TEXT_HTML_VALUE;
		}

	}

}
