package com.scheible.risingempire.mootheme.lbx;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import com.scheible.risingempire.mootheme.lbx.LbxEntry.Type;

/**
 *
 * @author sj
 */
public class LbxReader {

	public static <T> T read(final InputStream rawInput, final int entry, final Function<LbxEntry, T> entryFunction)
			throws IOException {
		try (LbxInputStream input = new LbxInputStream(rawInput)) {
			final int entries = input.readUShort();
			if (!(input.readUByte() == 0xad && input.readUByte() == 0xfe)) {
				throw new IllegalStateException("No valid LBX header was found!");
			}
			input.skip(2);
			final Type type = Type.valueOf(input.readUShort());

			long entryStart = 0;
			long entryEnd = -1;
			for (int i = 0; i <= entries; i++) {
				entryStart = entryEnd;
				entryEnd = input.readUInt();

				if (i - 1 == entry) {
					break;
				}
			}

			input.skip((int) (entryStart - input.getReadIndex()));

			return entryFunction.apply(new LbxEntry(input, entryStart, entryEnd, type));
		}
	}
}
