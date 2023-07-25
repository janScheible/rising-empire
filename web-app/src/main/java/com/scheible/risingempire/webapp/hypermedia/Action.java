package com.scheible.risingempire.webapp.hypermedia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import com.scheible.risingempire.util.jdk.Objects2;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class Action {

	private final List<ActionField> fields = new ArrayList<>();

	private final String name;
	private final String href;
	private final ActionHttpMethod method;
	@Nullable
	private final String contentType;

	public Action(final String name, final String href, final ActionHttpMethod method, final String contentType) {
		this.name = name;
		this.href = href;
		this.method = method;
		this.contentType = contentType;
	}

	public Action(final String name, final String href, final ActionHttpMethod method) {
		this(name, href, method, null);
	}

	public static Action jsonPost(final String name, final String... pathSegments) {
		return new Action(name,
				UriComponentsBuilder.newInstance().pathSegment(pathSegments).build().encode().toUriString(),
				ActionHttpMethod.POST, MediaType.APPLICATION_JSON_VALUE);
	}

	public static Action get(final String name, final String... pathSegments) {
		return new Action(name,
				UriComponentsBuilder.newInstance().pathSegment(pathSegments).build().encode().toUriString(),
				ActionHttpMethod.GET);
	}

	public static Action delete(final String name, final String... pathSegments) {
		return new Action(name,
				UriComponentsBuilder.newInstance().pathSegment(pathSegments).build().encode().toUriString(),
				ActionHttpMethod.DELETE);
	}

	public List<ActionField> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public ActionField getField(final String name) {
		return fields.stream().filter(ac -> ac.getName().equals(name)).findFirst().get();
	}

	public Action with(final ActionField field) {
		fields.add(field);
		return this;
	}

	public Action with(final Stream<ActionField> fieldStream) {
		fieldStream.forEach(f -> with(f));
		return this;
	}

	public Action with(final boolean predicate, final String name, final Supplier<Object> valueSupplier) {
		if (predicate) {
			final Object value = valueSupplier.get();
			if (value instanceof Stream) {
				((Stream<?>) value).forEach(v -> {
					with(name, v);
				});
			} else {
				with(name, value);
			}
		}

		return this;
	}

	public Action with(final boolean predicate, final Supplier<Stream<ActionField>> fieldStreamSupplier) {
		if (predicate) {
			return with(fieldStreamSupplier.get());
		} else {
			return this;
		}
	}

	public Action with(final String name, final Object value) {
		fields.add(new ActionField(name, value));
		return this;
	}

	public String toGetUri() {
		if (method != ActionHttpMethod.GET) {
			throw new IllegalStateException("Only GET actions can be converted to an Uri!");
		}

		final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance().path(href);
		fields.stream().forEach(f -> uriBuilder.queryParam(f.getName(), f.getValue().toString()));

		return uriBuilder.build().encode().toUriString();
	}

	public String getName() {
		return name;
	}

	public String getHref() {
		return href;
	}

	public ActionHttpMethod getMethod() {
		return method;
	}

	public Optional<String> getContentType() {
		return Optional.ofNullable(contentType);
	}

	@Override
	@SuppressFBWarnings(value = "COM_PARENT_DELEGATED_CALL", justification = "To satisfy the rule of having hashCode() implemented.")
	public int hashCode() {
		return Objects.hash(fields, name, href, method, contentType);
	}

	@Override
	@SuppressFBWarnings(value = "EQ_UNUSUAL", justification = "Object2.equals() is allowed.")
	public boolean equals(final Object obj) {
		return Objects2.equals(this, obj,
				other -> Objects.equals(fields, other.fields) && Objects.equals(name, other.name)
						&& Objects.equals(href, other.href) && Objects.equals(method, other.method)
						&& Objects.equals(contentType, other.contentType));
	}
}
