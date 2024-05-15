package com.scheible.risingempire.webapp.hypermedia;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.scheible.risingempire.util.jdk.Objects2;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author sj
 */
public class Action {

	private final List<ActionField> fields = new ArrayList<>();

	private final String name;

	/**
	 * Always encoded hyperlink reference suitable for serialization and sending to the
	 * Browser.
	 */
	private final String href;

	private final ActionHttpMethod method;

	private final Optional<String> contentType;

	/**
	 * @param name a name
	 * @param href the already encoded hyperlink reference
	 * @param method the HTTP method
	 * @param contentType the content type
	 */
	private Action(String name, String href, ActionHttpMethod method, Optional<String> contentType) {
		if (contentType.isPresent() && method != ActionHttpMethod.POST) {
			throw new IllegalStateException("Only POST actions have a content type!");
		}

		this.name = name;
		this.href = href;
		this.method = method;
		this.contentType = contentType;
	}

	/**
	 * @param name a name
	 * @param href the already encoded hyperlink reference
	 * @param method the HTTP method
	 */
	public Action(String name, String href, ActionHttpMethod method) {
		this(name, href, method, Optional.empty());
	}

	public static Action jsonPost(String name, String... pathSegments) {
		return new Action(name,
				UriComponentsBuilder.newInstance().pathSegment(pathSegments).encode().build().toUriString(),
				ActionHttpMethod.POST, Optional.of(MediaType.APPLICATION_JSON_VALUE));
	}

	public static Action get(String name, String... pathSegments) {
		return new Action(name,
				UriComponentsBuilder.newInstance().pathSegment(pathSegments).encode().build().toUriString(),
				ActionHttpMethod.GET);
	}

	public static Action delete(String name, String... pathSegments) {
		return new Action(name,
				UriComponentsBuilder.newInstance().pathSegment(pathSegments).encode().build().toUriString(),
				ActionHttpMethod.DELETE);
	}

	public List<ActionField> getFields() {
		return Collections.unmodifiableList(this.fields);
	}

	public ActionField getField(String name) {
		return this.fields.stream().filter(ac -> ac.getName().equals(name)).findFirst().get();
	}

	public Action with(ActionField field) {
		this.fields.add(field);
		return this;
	}

	public Action with(Stream<ActionField> fieldStream) {
		fieldStream.forEach(this::with);
		return this;
	}

	public Action with(boolean predicate, String name, Supplier<Object> valueSupplier) {
		if (predicate) {
			Object value = valueSupplier.get();
			if (value instanceof Stream) {
				((Stream<?>) value).forEach(v -> {
					with(name, v);
				});
			}
			else {
				with(name, value);
			}
		}

		return this;
	}

	public Action with(boolean predicate, Supplier<Stream<ActionField>> fieldStreamSupplier) {
		if (predicate) {
			return with(fieldStreamSupplier.get());
		}
		else {
			return this;
		}
	}

	public Action with(String name, Object value) {
		this.fields.add(new ActionField(name, value));
		return this;
	}

	public String toGetUri() {
		if (this.method != ActionHttpMethod.GET) {
			throw new IllegalStateException("Only GET actions can be converted to an Uri!");
		}

		// href (which is always encoded) is decoded first because it is encoded again by
		// UriComponentsBuilder
		String decodedHref = URLDecoder.decode(this.href, StandardCharsets.UTF_8);

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(decodedHref);
		this.fields.stream().forEach(f -> uriBuilder.queryParam(f.getName(), f.getValue().toString()));

		return uriBuilder.toUriString();
	}

	public String getName() {
		return this.name;
	}

	public String getHref() {
		return this.href;
	}

	public ActionHttpMethod getMethod() {
		return this.method;
	}

	public Optional<String> getContentType() {
		return this.contentType;
	}

	@Override
	public boolean equals(Object obj) {
		return Objects2.equals(this, obj,
				other -> Objects.equals(this.fields, other.fields) && Objects.equals(this.name, other.name)
						&& Objects.equals(this.href, other.href) && Objects.equals(this.method, other.method)
						&& Objects.equals(this.contentType, other.contentType));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.fields, this.name, this.href, this.method, this.contentType);
	}

}
