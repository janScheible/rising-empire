package com.scheible.risingempire.webapp.hypermedia;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
class ActionTest {

	@Test
	public void testToUri() {
		final Action action = Action.get("test", "first", "second", "third").with(new ActionField("foo", "bar")) //
				.with(new ActionField("encoded", "1+1=2"));

		assertThat(action.toGetUri()).isEqualTo("/first/second/third?foo=bar&encoded=1+1%3D2");
	}
}
