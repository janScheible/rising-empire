package com.scheible.risingempire.webapp.adapter.frontend;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.scheible.esbuild.bindings.util.ImportMapGenerator;
import com.scheible.esbuild.bindings.util.ImportMapper;
import com.scheible.esbuild.spring.AppRevision;
import com.scheible.risingempire.game.api.view.universe.Player;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author sj
 */
@Controller
@RequestMapping(produces = TEXT_HTML_VALUE)
class HtmlController {

	static class TemplateView implements View {

		private final String templatePath;
		private final Collection<Entry<String, Function<Map<String, ?>, String>>> replacements;

		public TemplateView(final String templatePath,
				final Collection<Entry<String, Function<Map<String, ?>, String>>> replacements) {
			this.templatePath = templatePath;
			this.replacements = replacements;
		}

		@Override
		public void render(final Map<String, ?> model, final HttpServletRequest request,
				final HttpServletResponse response) throws Exception {
			if (model != null) {
				response.setContentType(getContentType());

				final List<Entry<String, String>> evaluatedReplacement = replacements.stream()
						.map(rep -> Map.entry(rep.getKey(), rep.getValue().apply(model))).collect(Collectors.toList());
				final String renderedTemplate = render(new ClassPathResource(templatePath), evaluatedReplacement);
				response.getWriter().append(renderedTemplate);
			} else {
				response.sendError(INTERNAL_SERVER_ERROR.value());
			}
		}

		private String render(final Resource templateResource, final Collection<Entry<String, String>> replacements)
				throws IOException {
			try (InputStream input = templateResource.getInputStream()) {
				String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);

				for (final Entry<String, String> replacement : replacements) {
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

	private final AppRevision appRevision;
	private final ImportMapGenerator importMapGenerator;
	private final ServletContext servletContext;

	HtmlController(final AppRevision appRevision, final ImportMapGenerator importMapGenerator,
			final ServletContext servletContext) {
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
				new SimpleImmutableEntry<>("${importMap}", model -> this.importMapGenerator.generate(Map.of( //
						ImportMapper.FRONTEND_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/frontend", //
						ImportMapper.LIBRARY_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/lib", //
						ImportMapper.APP_REVISION_PLACEHOLDER, this.appRevision.value()))),
				new SimpleImmutableEntry<>("${appRevision}", model -> appRevision.value())));
	}

	@GetMapping(path = "/games/{gameId}/{player}")
	ModelAndView gameHtml(@PathVariable final String gameId, @PathVariable final Player player) {
		return new ModelAndView("gameView", Map.of("gameId", gameId, "player", player));
	}

	@Bean
	View gameView() {
		return new TemplateView("/templates/game.html", Arrays.asList( //
				new SimpleImmutableEntry<>("${gameId}", model -> model.get("gameId").toString()),
				new SimpleImmutableEntry<>("${urlEncodedGameId}", model -> urlEncode(model.get("gameId").toString())),
				new SimpleImmutableEntry<>("${player}", model -> model.get("player").toString()),
				new SimpleImmutableEntry<>("${importMap}", model -> this.importMapGenerator.generate(Map.of( //
						ImportMapper.FRONTEND_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/frontend", //
						ImportMapper.LIBRARY_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/lib", //
						ImportMapper.APP_REVISION_PLACEHOLDER, this.appRevision.value()))),
				new SimpleImmutableEntry<>("${appRevision}", model -> appRevision.value())));
	}

	@SuppressFBWarnings(value = "EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS", justification = "If encoding works once, it will work forever.")
	private static String urlEncode(final String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		} catch (final UnsupportedEncodingException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	@GetMapping(path = "/storybook.html", produces = TEXT_HTML_VALUE)
	ModelAndView storybookHtml() {
		return new ModelAndView("storybookView");
	}

	@Bean
	View storybookView() {
		return new TemplateView("/templates/storybook.html", Arrays.asList( //
				new SimpleImmutableEntry<>("${importMap}", model -> this.importMapGenerator.generate(Map.of( //
						ImportMapper.FRONTEND_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/frontend", //
						ImportMapper.LIBRARY_PREFIX_PLACEHOLDER, this.servletContext.getContextPath() + "/lib", //
						ImportMapper.APP_REVISION_PLACEHOLDER, this.appRevision.value()))),
				new SimpleImmutableEntry<>("${appRevision}", model -> appRevision.value())));
	}
}
