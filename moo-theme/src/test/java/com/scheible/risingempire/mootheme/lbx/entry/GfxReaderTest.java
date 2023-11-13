package com.scheible.risingempire.mootheme.lbx.entry;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.scheible.risingempire.mootheme.lbx.LbxEntry;
import com.scheible.risingempire.mootheme.lbx.LbxInputStream;
import org.junit.jupiter.api.Test;

import static com.scheible.risingempire.mootheme.lbx.LbxEntry.Type.GFX;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class GfxReaderTest {

	@Test
	void testSmileyGfx() throws IOException {
		ColorModel palette = new IndexColorModel(8, 4, new byte[] { (byte) 255, 0, (byte) 255, 0 },
				new byte[] { (byte) 255, 0, (byte) 255, 0 }, new byte[] { (byte) 255, 0, (byte) 64, (byte) 255 });

		/*
		 * - all columns except the first column (2 parts) either skip the column or have
		 * 1 part - all columns except the last one are compressed
		 */
		BufferedImage actual = GfxReader.read(
				new LbxEntry(new LbxInputStream(getClass().getResourceAsStream("smiley.gfx")), 0, 42, GFX), palette, 0);
		ImageIO.write(actual, "png", new File("./target/smiley.png"));

		BufferedImage expected = ImageIO.read(getClass().getResourceAsStream("smiley.png"));

		assertThat(actual.getWidth()).isEqualTo(expected.getWidth());
		assertThat(actual.getHeight()).isEqualTo(expected.getHeight());

		for (int x = 0; x < actual.getWidth(); x++) {
			for (int y = 0; y < actual.getHeight(); y++) {
				assertThat(actual.getRGB(x, y)).isEqualTo(expected.getRGB(x, y));
			}
		}
	}

}
