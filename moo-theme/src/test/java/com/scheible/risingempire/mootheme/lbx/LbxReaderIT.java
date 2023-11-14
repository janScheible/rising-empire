package com.scheible.risingempire.mootheme.lbx;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

import com.scheible.risingempire.mootheme.cli.MooThemeGeneratorCli;
import com.scheible.risingempire.mootheme.lbx.entry.GfxReader;
import com.scheible.risingempire.mootheme.lbx.entry.PaletteReader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author sj
 */
class LbxReaderIT {

	@Test
	@Disabled("Required a D-Fend Reloaded installation (on Windows) and is more of an integration test.")
	void testShipGfx() throws FileNotFoundException, IOException {
		ColorModel palette = LbxReader.read(
				new FileInputStream(new File(MooThemeGeneratorCli.DFEND_RELOADED_ORION_DIR, "FONTS.LBX")), 2,
				(LbxEntry lbxEntry) -> {
					try {
						return PaletteReader.read(lbxEntry);
					}
					catch (IOException ex) {
						throw new UncheckedIOException(ex);
					}
				});

		BufferedImage image = LbxReader.read(
				new FileInputStream(new File(MooThemeGeneratorCli.DFEND_RELOADED_ORION_DIR, "SHIPS.LBX")), 42,
				(LbxEntry lbxEntry) -> {
					try {
						return GfxReader.read(lbxEntry, palette, 0);
					}
					catch (IOException ex) {
						throw new UncheckedIOException(ex);
					}
				});
		ImageIO.write(image, "png", new File("./target/ship.png"));
	}

}
