package com.scheible.risingempire.webapp._hypermedia;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.scheible.risingempire.webapp.hypermedia.ActionField;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import static com.jayway.jsonpath.Option.AS_PATH_LIST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * @author sj
 */
public class HypermediaClient {

	private static final Logger logger = LoggerFactory.getLogger(HypermediaClient.class);

	private static final Configuration AS_PATH_LIST_CONFIG = Configuration.builder().options(AS_PATH_LIST).build();

	private final MockMvc mockMvc;

	private MvcResult page = null;

	private HypermediaClient(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	public static HypermediaClient create(String url, MediaType accept, MockMvc mockMvc) throws Exception {
		HypermediaClient client = new HypermediaClient(mockMvc);
		client.getInitial(get(url).accept(accept));
		return client;
	}

	public static HypermediaClient create(MockHttpServletRequestBuilder mockMvcRequestBuilder, MockMvc mockMvc)
			throws Exception {
		HypermediaClient client = new HypermediaClient(mockMvc);
		client.getInitial(mockMvcRequestBuilder);
		return client;
	}

	private MvcResult getInitial(MockHttpServletRequestBuilder mockMvcRequestBuilder) throws Exception {
		this.page = followRedirects(this.mockMvc.perform(mockMvcRequestBuilder).andReturn());
		return this.page;
	}

	public MvcResult submit(String actionJsonPath, ActionField... fields) throws Exception {
		if (this.page == null) {
			throw new IllegalStateException("getInitial(...) has to be called first!");
		}

		this.page = submitInternal(actionJsonPath, fields);
		return this.page;
	}

	private MvcResult submitInternal(String actionJsonPath, ActionField... clientFields) throws Exception {
		Object actions = JsonPath.parse(this.page.getResponse().getContentAsString()).read(actionJsonPath);
		@SuppressWarnings(value = "unchecked")
		Map<String, Object> action = actions instanceof JSONArray ? ((Map<String, Object>) ((JSONArray) actions).get(0))
				: (Map<String, Object>) actions;
		String method = action.get("method").toString();
		boolean post = "POST".equals(method);
		boolean get = "GET".equals(method);
		if (!post && !get) {
			throw new IllegalArgumentException("Unsupported HTTP method '" + method + "'!");
		}
		String url = action.get("href").toString();
		@SuppressWarnings(value = "unchecked")
		Set<Entry<String, Object>> fields = ((JSONArray) action.get("fields")).stream()
			.map(f -> ((Map<String, Object>) f))
			.map(f -> Map.entry(f.get("name").toString(), f.get("value")))
			.collect(Collectors.toSet());
		if (!post) {
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(action.get("href").toString());
			fields.forEach(f -> uriBuilder.queryParam(f.getKey(), f.getValue()));
			Stream.of(clientFields).forEach(cf -> uriBuilder.queryParam(cf.getName(), cf.getValue()));
			url = uriBuilder.build().encode().toString();
		}
		MockHttpServletRequestBuilder requestBuilder = post ? MockMvcRequestBuilders.post(url)
				: MockMvcRequestBuilders.get(url);

		Map<String, List<Object>> postContent = new HashMap<>();
		if (post) {
			requestBuilder.contentType(action.get("contentType").toString());
			fields.forEach(f -> postContent.computeIfAbsent(f.getKey(), key -> new ArrayList<>()).add(f.getValue()));
			Stream.of(clientFields)
				.forEach(cf -> postContent.computeIfAbsent(cf.getName(), key -> new ArrayList<>()).add(cf.getValue()));
			requestBuilder.content(JSONObject.toJSONString(postContent));
		}
		else if (get) {
			String accept = this.page.getResponse().getContentType() != null ? this.page.getResponse().getContentType()
					: this.page.getResponse().getHeader("Accept");
			requestBuilder.accept(accept);
		}

		logger.debug("submit action '{}' as {} to url {}{}", actionJsonPath, method, url,
				post ? " with body " + postContent : "");

		return followRedirects(this.mockMvc.perform(requestBuilder).andReturn());
	}

	private MvcResult followRedirects(MvcResult mvcResult) throws Exception {
		return followRedirects(mvcResult, 0);
	}

	private MvcResult followRedirects(MvcResult mvcResult, int counter) throws Exception {
		if (counter > 5) {
			throw new IllegalStateException("Redirect loop?");
		}
		if (mvcResult.getResponse().getStatus() == HttpStatus.SEE_OTHER.value()) {
			String accept = mvcResult.getRequest().getContentType() != null ? mvcResult.getRequest().getContentType()
					: mvcResult.getRequest().getHeader("Accept");
			return followRedirects(this.mockMvc
				.perform(MockMvcRequestBuilders.get(mvcResult.getResponse().getRedirectedUrl()).accept(accept))
				.andReturn(), counter + 1);
		}
		else {
			logger.debug("url {}",
					mvcResult.getRequest().getRequestURI() + "?" + mvcResult.getRequest().getQueryString());

			if (logger.isTraceEnabled()) {
				List<String> actionPaths = JsonPath
					.parse(mvcResult.getResponse().getContentAsString(), AS_PATH_LIST_CONFIG)
					.read("$.._actions[*].name");
				DocumentContext jsonPathParsedResponse = JsonPath.parse(mvcResult.getResponse().getContentAsString());

				logger.trace(actionPaths.stream()
					.map(ap -> ap.replaceAll("\\[\\d+\\]\\['name'\\]$",
							"[?(@.name=='" + jsonPathParsedResponse.read(ap) + "')]"))
					.sorted()
					.collect(Collectors.joining(", ")));
			}

			return mvcResult;
		}
	}

	public MvcResult getPage() {
		return this.page;
	}

	public String getPageContentAsString() throws UnsupportedEncodingException {
		return this.page.getResponse().getContentAsString();
	}

}
