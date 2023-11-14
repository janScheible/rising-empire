package com.scheible.risingempire.mootheme.lbx.entry;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;

import com.scheible.risingempire.mootheme.lbx.LbxEntry;
import com.scheible.risingempire.mootheme.lbx.LbxInputStream;

/**
 * @author sj
 */
public class GfxReader {

	@SuppressWarnings("PMD.UnusedLocalVariable")
	public static BufferedImage read(LbxEntry lbxEntry, ColorModel palette, int frame) throws IOException {
		if (LbxEntry.Type.GFX != lbxEntry.getType()) {
			throw new IllegalArgumentException("The lbx entry must be of type Gfx!");
		}

		LbxInputStream input = lbxEntry.getInput();
		long entryStart = lbxEntry.getEntryStart();

		int width = input.readUShort();
		int height = input.readUShort();
		int currentFrame = input.readUShort();
		int frames = input.readUShort();
		int resetFrame = input.readUShort();

		input.skip(4); // space for runtime data

		int paletteOffset = input.readUShort();
		if (paletteOffset != 0) {
			throw new UnsupportedOperationException("Internal palettes are not yet supported!");
		}
		boolean independentFrames = input.readUByte() != 0x0;
		boolean councilSpecialFormat = input.readUByte() != 0x0;

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

	@SuppressWarnings({ "PMD.UnusedLocalVariable", "PMD.EmptyControlStatement" })
	private static BufferedImage drawImage(LbxInputStream input, long entryStart, long frameStart, long frameEnd,
			int width, int height, ColorModel palette) throws IOException {
		input.skip((int) (entryStart + frameStart - input.getReadIndex()));
		WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 1, null);
		BufferedImage image = new BufferedImage(palette, raster, false, null);
		boolean clearBuffer = input.readUByte() == 0x1;
		int x = 0;
		while (input.getReadIndex() < entryStart + frameEnd) {
			int mode = input.readUByte();

			if (mode == 0xff) {
				// skip this column
			}
			else {
				boolean compressed = mode == 0x80;
				long sequenceLength = input.readUByte();

				int y = 0;
				do {
					int partLength = input.readUByte();

					int skipPixels = input.readUByte();
					y += skipPixels;

					sequenceLength -= partLength + 2;

					do {
						int colorOrRunLength = input.readUByte();
						partLength--;
						int color = colorOrRunLength;
						if (compressed && colorOrRunLength > 0xdf) {
							int runLength = colorOrRunLength - 0xdf;
							color = input.readUByte();
							partLength--;
							drawRun(runLength, raster, x, y, color);
							y += runLength;
						}
						else {
							drawSingle(raster, x, y, color);
							y++;
						}
					}
					while (partLength > 0);
				}
				while (sequenceLength > 0);
			}

			x++;
		}
		return image;
	}

	private static void drawRun(int length, WritableRaster raster, int x, int yStart, int color) {
		int y = yStart;
		for (int i = 0; i < length; i++) {
			raster.setSample(x, y, 0, color);
			y++;
		}
	}

	private static void drawSingle(WritableRaster raster, int x, int y, int color) {
		raster.setSample(x, y, 0, color);
	}

}
