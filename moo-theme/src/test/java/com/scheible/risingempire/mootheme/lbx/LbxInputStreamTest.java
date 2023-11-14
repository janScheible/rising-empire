package com.scheible.risingempire.mootheme.lbx;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author sj
 */
class LbxInputStreamTest {

	@Test
	void testRead() throws IOException {
		ByteArrayInputStream byteArrayInputStream = spy(
				new ByteArrayInputStream(new byte[] { 42, 42, 42, 42, 42, 42, 42, 0, 42 }));
		try (LbxInputStream input = new LbxInputStream(byteArrayInputStream)) {

			assertThat(input.readUByte()).isEqualTo((short) 42);
			assertThat(input.readUShort()).isEqualTo(10_794);
			assertThat(input.readUInt()).isEqualTo(707_406_378L);

			assertThat(input.skip(1)).isEqualTo(1);

			assertThat(input.readUByte()).isEqualTo((short) 42);
		}

		verify(byteArrayInputStream).close();
	}

}
