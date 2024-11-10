package com.scheible.risingempire.mootheme.binary;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class TextBinaryReaderTest {

	@Test
	public void testTexts() throws IOException {
		assertThat(new TextBinaryReader(3, 8, new char[] { 'F', 'S', 'T' })
			.read(getClass().getResourceAsStream("text.bin"))).containsOnly("First", "Second", "Third");
	}

}