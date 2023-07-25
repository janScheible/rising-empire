package com.scheible.risingempire.mootheme.lbx.entry;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;

import com.scheible.risingempire.mootheme.lbx.LbxEntry;
import com.scheible.risingempire.mootheme.lbx.LbxInputStream;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class GfxReader {

	@SuppressWarnings({ "PMD.UnusedLocalVariable", "PMD.EmptyIfStmt" })
	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
	public static BufferedImage read(final LbxEntry lbxEntry, final ColorModel palette, final int frame)
			throws IOException {
		if (LbxEntry.Type.GFX != lbxEntry.getType()) {
			throw new IllegalArgumentException("The lbx entry must be of type Gfx!");
		}

		final LbxInputStream input = lbxEntry.getInput();
		final long entryStart = lbxEntry.getEntryStart();

		final int width = input.readUShort();
		final int height = input.readUShort();
		final int currentFrame = input.readUShort();
		final int frames = input.readUShort();
		final int resetFrame = input.readUShort();

		input.skip(4); // space for runtime data

		final int paletteOffset = input.readUShort();
		if (paletteOffset != 0) {
			throw new UnsupportedOperationException("Internal palettes are not yet supported!");
		}
		final boolean independentFrames = input.readUByte() != 0x0;
		final boolean councilSpecialFormat = input.readUByte() != 0x0;

		long frameStart = 0;
		long frameEnd = -1;
		for (int i = 0; i <= frames; i++) {
			frameStart = frameEnd;
			frameEnd = input.readUInt();

			if (i - 1 == frame) {
				break;
			}
		}

		return drawImage(input, entryStart, frameStart, frameEnd, width, height, palette);
	}

	@SuppressWarnings({ "PMD.UnusedLocalVariable", "PMD.EmptyIfStmt", "PMD.EmptyControlStatement" })
	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
	private static BufferedImage drawImage(final LbxInputStream input, final long entryStart, final long frameStart,
			final long frameEnd, final int width, final int height, final ColorModel palette) throws IOException {
		input.skip((int) (entryStart + frameStart - input.getReadIndex()));
		final WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 1, null);
		final BufferedImage image = new BufferedImage(palette, raster, false, null);
		final boolean clearBuffer = input.readUByte() == 0x1;
		int x = 0;
		while (input.getReadIndex() < entryStart + frameEnd) {
			final int mode = input.readUByte();

			if (mode == 0xff) {
				// skip this column
			} else {
				final boolean compressed = mode == 0x80;
				long sequenceLength = input.readUByte();

				int y = 0;
				do {
					int partLength = input.readUByte();

					final int skipPixels = input.readUByte();
					y += skipPixels;

					sequenceLength -= partLength + 2;

					do {
						final int colorOrRunLength = input.readUByte();
						partLength--;
						int color = colorOrRunLength;
						if (compressed && colorOrRunLength > 0xdf) {
							final int runLength = colorOrRunLength - 0xdf;
							color = input.readUByte();
							partLength--;
							drawRun(runLength, raster, x, y, color);
							y += runLength;
						} else {
							drawSingle(raster, x, y, color);
							y++;
						}
					} while (partLength > 0);
				} while (sequenceLength > 0);
			}

			x++;
		}
		return image;
	}

	private static void drawRun(final int length, final WritableRaster raster, final int x, final int yStart,
			final int color) {
		int y = yStart;
		for (int i = 0; i < length; i++) {
			raster.setSample(x, y, 0, color);
			y++;
		}
	}

	private static void drawSingle(final WritableRaster raster, final int x, final int y, final int color) {
		raster.setSample(x, y, 0, color);
	}
}
