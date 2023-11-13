package com.scheible.risingempire.mootheme.lbx.entry;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.IOException;

import com.scheible.risingempire.mootheme.lbx.LbxEntry;
import com.scheible.risingempire.mootheme.lbx.LbxInputStream;

/**
 * @author sj
 */
public class PaletteReader {

	public static ColorModel read(final LbxEntry lbxEntry) throws IOException {
		if (LbxEntry.Type.FONTS_OR_PALLETS != lbxEntry.getType()) {
			throw new IllegalArgumentException("The lbx entry must be of type font or pallets!");
		}

		final LbxInputStream input = lbxEntry.getInput();

		final byte[] red = new byte[256];
		final byte[] green = new byte[256];
		final byte[] blue = new byte[256];

		for (int i = 0; i < 256; i++) {
			// all color values are in the range of 0..63 (6bpp per item) --> multiply by
			// 4 (otherwise the colors
			// are pretty dark)
			red[i] = (byte) (input.readUByte() * 4);
			green[i] = (byte) (input.readUByte() * 4);
			blue[i] = (byte) (input.readUByte() * 4);
		}

		return new IndexColorModel(8, 256, red, green, blue);
	}

}
