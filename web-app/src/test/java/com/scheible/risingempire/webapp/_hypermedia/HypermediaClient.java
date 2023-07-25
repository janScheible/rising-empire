package com.scheible.risingempire.webapp._hypermedia;

import static com.jayway.jsonpath.Option.AS_PATH_LIST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.scheible.risingempire.webapp.hypermedia.ActionField;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 *
 * @author sj
 */
public class HypermediaClient {

	private static final Logger logger = LoggerFactory.getLogger(HypermediaClient.class);

	private static final Configuration AS_PATH_LIST_CONFIG = Configuration.builder().options(AS_PATH_LIST).build();

	private final MockMvc mockMvc;
	private MvcResult page = null;

	private HypermediaClient(final MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	public static HypermediaClient create(final String url, final MediaType accept, final MockMvc mockMvc)
			throws Exception {
		final HypermediaClient client = new HypermediaClient(mockMvc);
		client.getInitial(get(url).accept(accept));
		return client;
	}

	public static HypermediaClient create(final MockHttpServletRequestBuilder mockMvcRequestBuilder,
			final MockMvc mockMvc) throws Exception {
		final HypermediaClient client = new HypermediaClient(mockMvc);
		client.getInitial(mockMvcRequestBuilder);
		return client;
	}

	private MvcResult getInitial(final MockHttpServletRequestBuilder mockMvcRequestBuilder) throws Exception {
		page = followRedirects(mockMvc.perform(mockMvcRequestBuilder).andReturn());
		return page;
	}

	public MvcResult submit(final String actionJsonPath, ActionField... fields) throws Exception {
		if (page == null) {
			throw new IllegalStateException("getInitial(...) has to be called first!");
		}

		page = submitInternal(actionJsonPath, fields);
		return page;
	}

	private MvcResult submitInternal(final String actionJsonPath, ActionField... clientFields) throws Exception {
		final Object actions = JsonPath.parse(page.getResponse().getContentAsString()).read(actionJsonPath);
		@SuppressWarnings(value = "unchecked")
		final Map<String, Object> action = actions instanceof JSONArray
				? ((Map<String, Object>) ((JSONArray) actions).get(0))
				: (Map<String, Object>) actions;
		final String method = action.get("method").toString();
		final boolean post = "POST".equals(method);
		final boolean get = "GET".equals(method);
		if (!post && !get) {
			throw new IllegalArgumentException("Unsupported HTTP method '" + method + "'!");
		}
		String url = action.get("href").toString();
		@SuppressWarnings(value = "unchecked")
		final Set<Entry<String, Object>> fields = ((JSONArray) action.get("fields")).stream()
				.map(f -> ((Map<String, Object>) f))
				.map(f -> new AbstractMap.SimpleImmutableEntry<>(f.get("name").toString(), f.get("value")))
				.collect(Collectors.toSet());
		if (!post) {
			final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(action.get("href").toString());
			fields.forEach(f -> uriBuilder.queryParam(f.getKey(), f.getValue()));
			Stream.of(clientFields).forEach(cf -> uriBuilder.queryParam(cf.getName(), cf.getValue()));
			url = uriBuilder.build().encode().toString();
		}
		final MockHttpServletRequestBuilder requestBuilder = post ? MockMvcRequestBuilders.post(url)
				: MockMvcRequestBuilders.get(url);

		final Map<String, List<Object>> postContent = new HashMap<>();
		if (post) {
			requestBuilder.contentType(action.get("contentType").toString());
			fields.forEach(f -> postContent.computeIfAbsent(f.getKey(), key -> new ArrayList<>()).add(f.getValue()));
			Stream.of(clientFields).forEach(
					cf -> postContent.computeIfAbsent(cf.getName(), key -> new ArrayList<>()).add(cf.getValue()));
			requestBuilder.content(JSONObject.toJSONString(postContent));
		} else if (get) {
			final String accept = page.getResponse().getContentType() != null ? page.getResponse().getContentType()
					: page.getResponse().getHeader("Accept");
			requestBuilder.accept(accept);
		}

		logger.debug("submit action '{}' as {} to url {}{}", actionJsonPath, method, url,
				post ? " with body " + postContent : "");

		return followRedirects(mockMvc.perform(requestBuilder).andReturn());
	}

	private MvcResult followRedirects(final MvcResult mvcResult) throws Exception {
		return followRedirects(mvcResult, 0);
	}

	private MvcResult followRedirects(final MvcResult mvcResult, final int counter) throws Exception {
		if (counter > 5) {
			throw new IllegalStateException("Redirect loop?");
		}
		if (mvcResult.getResponse().getStatus() == HttpStatus.SEE_OTHER.value()) {
			final String accept = mvcResult.getRequest().getContentType() != null
					? mvcResult.getRequest().getContentType()
					: mvcResult.getRequest().getHeader("Accept");
			return followRedirects(mockMvc
					.perform(MockMvcRequestBuilders.get(mvcResult.getResponse().getRedirectedUrl()).accept(accept))
					.andReturn(), counter + 1);
		} else {
			logger.debug("url {}",
					mvcResult.getRequest().getRequestURI() + "?" + mvcResult.getRequest().getQueryString());

			if (logger.isTraceEnabled()) {
				final List<String> actionPaths = JsonPath
						.parse(mvcResult.getResponse().getContentAsString(), AS_PATH_LIST_CONFIG)
						.read("$.._actions[*].name");
				final DocumentContext jsonPathParsedResponse = JsonPath
						.parse(mvcResult.getResponse().getContentAsString());

				logger.trace(actionPaths.stream()
						.map(ap -> ap.replaceAll("\\[\\d+\\]\\['name'\\]$",
								"[?(@.name=='" + jsonPathParsedResponse.read(ap) + "')]"))
						.sorted().collect(Collectors.joining(", ")));
			}

			return mvcResult;
		}
	}

	public MvcResult getPage() {
		return page;
	}

	public String getPageContentAsString() throws UnsupportedEncodingException {
		return page.getResponse().getContentAsString();
	}
}
