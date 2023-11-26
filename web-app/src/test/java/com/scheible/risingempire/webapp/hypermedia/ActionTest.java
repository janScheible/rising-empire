package com.scheible.risingempire.webapp.hypermedia;

import org.junit.jupiter.api.Test;

import static com.scheible.risingempire.webapp.hypermedia.ActionHttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class ActionTest {

	@Test
	void testConstructorToUri() {
		assertThat(Action.get("test", "first", "second", "third")
			.with(new ActionField("foo", "bar")) //
			.with(new ActionField("encoded", "1+1=2"))
			.toGetUri()).isEqualTo("/first/second/third?foo=bar&encoded=1+1%3D2");
	}

	@Test
	void testConstructorWithEncodedHrefToUri() {
		assertThat(new Action("test", "/frontend/t%C3%B6st/YELLOW", GET).with(new ActionField("foo", "bar")) //
			.with(new ActionField("encoded", "1+1=2"))
			.toGetUri()).isEqualTo("/frontend/t%C3%B6st/YELLOW?foo=bar&encoded=1+1%3D2");
	}

	@Test
	void testGetToUri() {
		assertThat(Action.get("test", "first", "test")
			.with(new ActionField("foo", "bar")) //
			.with(new ActionField("encoded", "1+1=2"))
			.toGetUri()) //
			.isEqualTo("/first/test?foo=bar&encoded=1+1%3D2");
	}

	@Test
	void testGetWithEncodingNeededToUri() {
		assertThat(Action.get("test", "first", "töst")
			.with(new ActionField("foo", "bar")) //
			.with(new ActionField("encoded", "1+1=2"))
			.toGetUri()).isEqualTo("/first/t%C3%B6st?foo=bar&encoded=1+1%3D2");
	}

}
