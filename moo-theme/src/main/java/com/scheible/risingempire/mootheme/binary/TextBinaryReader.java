package com.scheible.risingempire.mootheme.binary;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sj
 */
public class TextBinaryReader {

	private final int entryCount;

	private final int entryBytes;

	// the intial letters of the entries we are searching for
	private final char[] initialLetters;

	private final int[] ringBuffer;

	@SuppressWarnings("PMD.UseVarargs")
	public TextBinaryReader(int entryCount, int entryBytes, char[] initialLetters) {
		this.entryCount = entryCount;
		this.entryBytes = entryBytes;
		this.initialLetters = initialLetters;

		this.ringBuffer = new int[this.entryCount * this.entryBytes];
	}

	@SuppressWarnings("PMD.AvoidReassigningLoopVariables")
	public Set<String> read(InputStream rawInput) throws IOException {
		Set<String> result = new HashSet<>();

		int i = -1;
		try (DataInputStream dataInput = new DataInputStream(new BufferedInputStream(rawInput))) {
			int value;
			while ((value = dataInput.read()) != -1) {
				this.ringBuffer[++i % this.ringBuffer.length] = value;

				boolean initialsMatch = true;
				for (int k = 1; k <= this.initialLetters.length; k++) {
					if (this.ringBuffer[index(i,
							(this.initialLetters.length - k) * -this.entryBytes)] != this.initialLetters[k - 1]) {
						initialsMatch = false;
						break;
					}
				}

				if (initialsMatch) {
					// finish reading the last entry
					for (int j = 0; j < this.entryBytes - 1; j++) {
						value = dataInput.read();
						this.ringBuffer[++i % this.ringBuffer.length] = value;
					}

					// now the ring buffer contains all entries that can be extracted now
					StringBuilder entry = new StringBuilder();
					for (int l = 0; l < this.entryCount * this.entryBytes; l++) {
						int currentValue = this.ringBuffer[index(i, -this.entryCount * this.entryBytes + (l + 1))];
						// System.out.println((char) currentValue + " (" + currentValue +
						// ")");
						if (currentValue != 0x0) {
							entry.append((char) currentValue);
						}
						else {
							int entryBytesLeft = this.entryBytes - (l % this.entryBytes);
							l += entryBytesLeft - 1;

							result.add(entry.toString().trim());
							entry.setLength(0);
						}
					}

					break;
				}
			}
		}

		return result;
	}

	private int index(int i, int offset) {
		int newIndex = (i + offset) % this.ringBuffer.length;
		return newIndex < 0 ? this.ringBuffer.length + newIndex : newIndex;
	}

}
