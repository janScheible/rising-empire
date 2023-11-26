package com.scheible.risingempire.mootheme.cli;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import com.scheible.risingempire.mootheme.canvas.Paintable;
import com.scheible.risingempire.mootheme.lbx.LbxEntry;
import com.scheible.risingempire.mootheme.lbx.LbxReader;
import com.scheible.risingempire.mootheme.lbx.entry.GfxReader;
import com.scheible.risingempire.mootheme.lbx.entry.PaletteReader;
import com.scheible.risingempire.mootheme.processor.ImageProcessor;
import com.scheible.risingempire.mootheme.processor.ImageProcessor.Scale;
import com.scheible.risingempire.mootheme.sprite.SpriteSheetGenerator;
import com.scheible.risingempire.mootheme.util.ExitUtils;
import com.scheible.risingempire.mootheme.util.WrappedLogger;

/**
 * @author sj
 */
public class MooThemeGeneratorCli {

	public static final File DFEND_RELOADED_ORION_DIR = new File(
			System.getProperty("user.home") + "\\D-Fend Reloaded\\VirtualHD\\orion");

	private static final String[] LBX_FILES = { "FONTS.LBX", "PLANETS.LBX", "SHIPS.LBX", "SHIPS2.LBX", "COLONIES.LBX",
			"STARMAP.LBX" };

	// NOTE Java util logger is fine here because JAR should have no third-party
	// dependencies.
	private static final WrappedLogger logger = WrappedLogger.getLogger(MooThemeGeneratorCli.class);

	public static void main(String... args) throws IOException {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");

		try {
			File orionDir = getAndValidateOrionDir(args);

			ColorModel palette = LbxReader.read(Files.newInputStream(Path.of(orionDir.getAbsolutePath(), "FONTS.LBX")),
					2, (LbxEntry lbxEntry) -> {
						try {
							return PaletteReader.read(lbxEntry);
						}
						catch (IOException ex) {
							throw new UncheckedIOException(ex);
						}
					});

			InputStream planets = new ByteArrayInputStream(
					Files.readAllBytes(Path.of(orionDir.getAbsolutePath(), "PLANETS.LBX")));
			InputStream ships = new ByteArrayInputStream(
					Files.readAllBytes(Path.of(orionDir.getAbsolutePath(), "SHIPS.LBX")));
			InputStream ships2 = new ByteArrayInputStream(
					Files.readAllBytes(Path.of(orionDir.getAbsolutePath(), "SHIPS2.LBX")));
			InputStream starmap = new ByteArrayInputStream(
					Files.readAllBytes(Path.of(orionDir.getAbsolutePath(), "STARMAP.LBX")));

			BiFunction<InputStream, Integer, BufferedImage> getImage = (input, entry) -> {
				try {
					input.mark(0);
					BufferedImage result = LbxReader.read(input, entry, lbxEntry -> {
						try {
							return GfxReader.read(lbxEntry, palette, 0);
						}
						catch (IOException ex) {
							throw new UncheckedIOException(ex);
						}
					});
					input.reset();
					return result;
				}
				catch (IOException ex) {
					throw new UncheckedIOException(ex);
				}
			};

			BufferedImage fleetsSheet = SpriteSheetGenerator.generate(3, true,
					List.of(process(getImage.apply(starmap, 67), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 68), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 69), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 70), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 71), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 72), Scale.TRIPLE, -16_777_216)));

			BufferedImage starsSheet = SpriteSheetGenerator.generate(3, true,
					List.of(process(getImage.apply(starmap, 9), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 10), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 11), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 12), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 13), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 14), Scale.TRIPLE, -16_777_216)));

			BufferedImage starsSmallSheet = SpriteSheetGenerator.generate(3, true,
					List.of(process(getImage.apply(starmap, 3), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 4), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 5), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 6), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 7), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(starmap, 8), Scale.TRIPLE, -16_777_216)));

			BufferedImage shipsBlueSheet = SpriteSheetGenerator.generate(3, true,
					List.of(process(getImage.apply(ships2, 0), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 1), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 2), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 3), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 4), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 5), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(ships2, 6), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 7), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 8), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 9), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 10), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 11), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(ships2, 12), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 13), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 14), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 15), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 16), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 17), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(ships2, 18), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 19), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 20), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 21), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 22), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships2, 23), Scale.TRIPLE, -16_777_216)));

			BufferedImage shipsWhiteSheet = SpriteSheetGenerator.generate(3, true,
					List.of(process(getImage.apply(ships, 24), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 25), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 26), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 27), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 28), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 29), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(ships, 30), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 31), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 32), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 33), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 34), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 35), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(ships, 36), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 37), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 38), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 39), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 40), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 41), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(ships, 42), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 43), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 44), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 45), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 46), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 47), Scale.TRIPLE, -16_777_216)));

			BufferedImage shipsYellowSheet = SpriteSheetGenerator.generate(3, true,
					List.of(process(getImage.apply(ships, 48), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 49), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 50), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 51), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 52), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 53), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(ships, 54), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 55), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 56), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 57), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 58), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 59), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(ships, 60), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 61), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 62), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 63), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 64), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 65), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(ships, 67), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 68), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 69), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 70), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 71), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(ships, 72), Scale.TRIPLE, -16_777_216)));

			BufferedImage planetsSheet = SpriteSheetGenerator.generate(3, true,
					List.of(process(getImage.apply(planets, 0), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 1), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 2), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 3), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 4), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 5), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 6), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(planets, 7), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 8), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 9), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 10), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 11), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 12), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 13), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(planets, 14), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 15), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 16), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 17), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 18), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 19), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 20), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(planets, 21), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 22), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 23), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 24), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 25), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 26), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 27), Scale.TRIPLE, -16_777_216)),
					List.of(process(getImage.apply(planets, 28), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 29), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 30), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 31), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 32), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 33), Scale.TRIPLE, -16_777_216),
							process(getImage.apply(planets, 34), Scale.TRIPLE, -16_777_216)));

			File targetDir = Optional.of(new File(".").getCanonicalFile())
				.map(wd -> "target".equalsIgnoreCase(wd.getName()) ? wd : new File(wd, "target"))
				.get();
			Path themeZip = Path.of(targetDir.getAbsolutePath(), "moo-theme.zip");

			writeZipFile(themeZip, sheetEntry("fleets.png", fleetsSheet), sheetEntry("stars.png", starsSheet),
					sheetEntry("stars-small.png", starsSmallSheet), sheetEntry("ships-blue.png", shipsBlueSheet),
					sheetEntry("ships-white.png", shipsWhiteSheet), sheetEntry("ships-yellow.png", shipsYellowSheet),
					sheetEntry("planets.png", planetsSheet));
			logger.info("Wrote ZIP file to ''{0}''.", themeZip);
		}
		catch (UncheckedIOException ex) {
			throw ex.getCause();
		}
	}

	private static File getAndValidateOrionDir(String... args) {
		Optional<File> dfendReloadedDir = Optional.of(DFEND_RELOADED_ORION_DIR).filter(File::exists);
		Optional<File> argsDir = Optional.ofNullable(args.length > 0 && args[0] != null ? new File(args[0]) : null)
			.filter(File::exists);

		File orionDir = null;
		if (argsDir.isEmpty() && dfendReloadedDir.isPresent()) {
			logger.info(
					"No valid orion dir was passed as argument but D-Fend Reloaded installation was found (''{0}'').",
					dfendReloadedDir.get());
			orionDir = dfendReloadedDir.get();
		}
		else if (argsDir.isEmpty() && dfendReloadedDir.isEmpty()) {
			logger.error(
					"Neither was a valid orion dir passed as argument nor was a D-Fend Reloaded installation found (''{0}'').",
					dfendReloadedDir.get());
			ExitUtils.exitFailed();
		}
		else if (argsDir.isPresent() && dfendReloadedDir.isPresent()) {
			logger.info(
					"Orion dir passed as argument (''{0}'') is used. The D-Fend Reloaded installation (''{1}'') is ignored.",
					new Object[] { argsDir.get(), dfendReloadedDir.get() });
			orionDir = argsDir.get();
		}
		else if (argsDir.isPresent() && dfendReloadedDir.isEmpty()) {
			logger.info("Orion dir passed as argument (''{0}'') is used.", argsDir.get());
			orionDir = argsDir.get();
		}

		for (String lbxFileName : LBX_FILES) {
			File lbxFile = new File(orionDir, lbxFileName);
			if (!lbxFile.exists()) {
				logger.error(
						"The LBX file ''{0}'' was not found in the orion directory. Is the directory a valid Master of Orion installation?",
						lbxFileName);
				ExitUtils.exitFailed();
			}
		}

		return orionDir;
	}

	@SafeVarargs
	private static void writeZipFile(Path targetDir, Entry<String, BufferedImage>... entries) throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(targetDir))) {
			Set<String> processed = new HashSet<>();

			for (Entry<String, BufferedImage> entry : entries) {
				String path = entry.getKey();

				String currentPath = "";
				for (String fragment : path.split("/")) {
					currentPath = currentPath + fragment + (!fragment.contains(".") ? "/" : "");

					if (fragment.contains(".")) {
						ZipEntry zipEntry = new ZipEntry(currentPath);
						zos.putNextEntry(zipEntry);

						BufferedImage image = entry.getValue();
						ByteArrayOutputStream baos = new ByteArrayOutputStream(image.getWidth() * image.getHeight());
						ImageIO.write(image, "png", baos);
						zos.write(baos.toByteArray());
						zos.closeEntry();
					}
					else if (processed.add(currentPath)) {
						ZipEntry zipEntry = new ZipEntry(currentPath);
						zos.putNextEntry(zipEntry);
					}
				}
			}
		}
	}

	private static Entry<String, BufferedImage> sheetEntry(String path, BufferedImage image) {
		return Map.entry(path, image);
	}

	private static Paintable process(BufferedImage image, Scale scale, Integer transparentColor) {
		return ImageProcessor.process(image, scale, transparentColor);
	}

}
