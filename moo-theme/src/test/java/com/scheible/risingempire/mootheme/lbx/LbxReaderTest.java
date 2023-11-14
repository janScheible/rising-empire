package com.scheible.risingempire.mootheme.lbx;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author sj
 */
class LbxReaderTest {

	@Test
	void testInvalid() throws IOException {
		assertThatThrownBy(
				() -> LbxReader.read(getClass().getResourceAsStream("invalid.lbx"), 0, (LbxEntry lbxEntry) -> null))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("No valid LBX header was found!");
	}

	@Test
	void testValid() throws IOException {
		LbxEntry entry = LbxReader.read(getClass().getResourceAsStream("valid.lbx"), 1,
				(LbxEntry lbxEntry) -> lbxEntry);
		assertThat(entry.getEntryStart()).isEqualTo(122);
		assertThat(entry.getEntryEnd()).isEqualTo(220);
	}

}
