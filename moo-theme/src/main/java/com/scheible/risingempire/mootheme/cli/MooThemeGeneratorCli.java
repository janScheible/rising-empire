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
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static com.scheible.risingempire.mootheme.processor.ImageProcessor.Scale.TRIPLE;
import static java.util.Arrays.asList;

/**
 * @author sj
 */
@SuppressFBWarnings("PATH_TRAVERSAL_IN")
public class MooThemeGeneratorCli {

	public static final File DFEND_RELOADED_ORION_DIR = new File(
			System.getProperty("user.home") + "\\D-Fend Reloaded\\VirtualHD\\orion");

	private static final String[] LBX_FILES = { "FONTS.LBX", "PLANETS.LBX", "SHIPS.LBX", "SHIPS2.LBX", "COLONIES.LBX",
			"STARMAP.LBX" };

	// NOTE Java util logger is fine here because JAR should have no third-party
	// dependencies.
	private static final Logger logger = Logger.getLogger(MooThemeGeneratorCli.class.getName());

	public static void main(final String... args) throws IOException {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");

		try {
			final File orionDir = getAndValidateOrionDir(args);

			final ColorModel palette = LbxReader.read(
					Files.newInputStream(Path.of(orionDir.getAbsolutePath(), "FONTS.LBX")), 2, (LbxEntry lbxEntry) -> {
						try {
							return PaletteReader.read(lbxEntry);
						}
						catch (IOException ex) {
							throw new UncheckedIOException(ex);
						}
					});

			final InputStream planets = new ByteArrayInputStream(
					Files.readAllBytes(Path.of(orionDir.getAbsolutePath(), "PLANETS.LBX")));
			final InputStream ships = new ByteArrayInputStream(
					Files.readAllBytes(Path.of(orionDir.getAbsolutePath(), "SHIPS.LBX")));
			final InputStream ships2 = new ByteArrayInputStream(
					Files.readAllBytes(Path.of(orionDir.getAbsolutePath(), "SHIPS2.LBX")));
			final InputStream starmap = new ByteArrayInputStream(
					Files.readAllBytes(Path.of(orionDir.getAbsolutePath(), "STARMAP.LBX")));

			final BiFunction<InputStream, Integer, BufferedImage> getImage = (input, entry) -> {
				try {
					input.mark(0);
					final BufferedImage result = LbxReader.read(input, entry, lbxEntry -> {
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

			final BufferedImage fleetsSheet = SpriteSheetGenerator.generate(3, true,
					asList(process(getImage.apply(starmap, 67), TRIPLE, -16777216),
							process(getImage.apply(starmap, 68), TRIPLE, -16777216),
							process(getImage.apply(starmap, 69), TRIPLE, -16777216),
							process(getImage.apply(starmap, 70), TRIPLE, -16777216),
							process(getImage.apply(starmap, 71), TRIPLE, -16777216),
							process(getImage.apply(starmap, 72), TRIPLE, -16777216)));

			final BufferedImage starsSheet = SpriteSheetGenerator.generate(3, true,
					asList(process(getImage.apply(starmap, 9), TRIPLE, -16777216),
							process(getImage.apply(starmap, 10), TRIPLE, -16777216),
							process(getImage.apply(starmap, 11), TRIPLE, -16777216),
							process(getImage.apply(starmap, 12), TRIPLE, -16777216),
							process(getImage.apply(starmap, 13), TRIPLE, -16777216),
							process(getImage.apply(starmap, 14), TRIPLE, -16777216)));

			final BufferedImage starsSmallSheet = SpriteSheetGenerator.generate(3, true,
					asList(process(getImage.apply(starmap, 3), TRIPLE, -16777216),
							process(getImage.apply(starmap, 4), TRIPLE, -16777216),
							process(getImage.apply(starmap, 5), TRIPLE, -16777216),
							process(getImage.apply(starmap, 6), TRIPLE, -16777216),
							process(getImage.apply(starmap, 7), TRIPLE, -16777216),
							process(getImage.apply(starmap, 8), TRIPLE, -16777216)));

			final BufferedImage shipsBlueSheet = SpriteSheetGenerator.generate(3, true,
					asList(process(getImage.apply(ships2, 0), TRIPLE, -16777216),
							process(getImage.apply(ships2, 1), TRIPLE, -16777216),
							process(getImage.apply(ships2, 2), TRIPLE, -16777216),
							process(getImage.apply(ships2, 3), TRIPLE, -16777216),
							process(getImage.apply(ships2, 4), TRIPLE, -16777216),
							process(getImage.apply(ships2, 5), TRIPLE, -16777216)),
					asList(process(getImage.apply(ships2, 6), TRIPLE, -16777216),
							process(getImage.apply(ships2, 7), TRIPLE, -16777216),
							process(getImage.apply(ships2, 8), TRIPLE, -16777216),
							process(getImage.apply(ships2, 9), TRIPLE, -16777216),
							process(getImage.apply(ships2, 10), TRIPLE, -16777216),
							process(getImage.apply(ships2, 11), TRIPLE, -16777216)),
					asList(process(getImage.apply(ships2, 12), TRIPLE, -16777216),
							process(getImage.apply(ships2, 13), TRIPLE, -16777216),
							process(getImage.apply(ships2, 14), TRIPLE, -16777216),
							process(getImage.apply(ships2, 15), TRIPLE, -16777216),
							process(getImage.apply(ships2, 16), TRIPLE, -16777216),
							process(getImage.apply(ships2, 17), TRIPLE, -16777216)),
					asList(process(getImage.apply(ships2, 18), TRIPLE, -16777216),
							process(getImage.apply(ships2, 19), TRIPLE, -16777216),
							process(getImage.apply(ships2, 20), TRIPLE, -16777216),
							process(getImage.apply(ships2, 21), TRIPLE, -16777216),
							process(getImage.apply(ships2, 22), TRIPLE, -16777216),
							process(getImage.apply(ships2, 23), TRIPLE, -16777216)));

			final BufferedImage shipsWhiteSheet = SpriteSheetGenerator.generate(3, true,
					asList(process(getImage.apply(ships, 24), TRIPLE, -16777216),
							process(getImage.apply(ships, 25), TRIPLE, -16777216),
							process(getImage.apply(ships, 26), TRIPLE, -16777216),
							process(getImage.apply(ships, 27), TRIPLE, -16777216),
							process(getImage.apply(ships, 28), TRIPLE, -16777216),
							process(getImage.apply(ships, 29), TRIPLE, -16777216)),
					asList(process(getImage.apply(ships, 30), TRIPLE, -16777216),
							process(getImage.apply(ships, 31), TRIPLE, -16777216),
							process(getImage.apply(ships, 32), TRIPLE, -16777216),
							process(getImage.apply(ships, 33), TRIPLE, -16777216),
							process(getImage.apply(ships, 34), TRIPLE, -16777216),
							process(getImage.apply(ships, 35), TRIPLE, -16777216)),
					asList(process(getImage.apply(ships, 36), TRIPLE, -16777216),
							process(getImage.apply(ships, 37), TRIPLE, -16777216),
							process(getImage.apply(ships, 38), TRIPLE, -16777216),
							process(getImage.apply(ships, 39), TRIPLE, -16777216),
							process(getImage.apply(ships, 40), TRIPLE, -16777216),
							process(getImage.apply(ships, 41), TRIPLE, -16777216)),
					asList(process(getImage.apply(ships, 42), TRIPLE, -16777216),
							process(getImage.apply(ships, 43), TRIPLE, -16777216),
							process(getImage.apply(ships, 44), TRIPLE, -16777216),
							process(getImage.apply(ships, 45), TRIPLE, -16777216),
							process(getImage.apply(ships, 46), TRIPLE, -16777216),
							process(getImage.apply(ships, 47), TRIPLE, -16777216)));

			final BufferedImage shipsYellowSheet = SpriteSheetGenerator.generate(3, true,
					asList(process(getImage.apply(ships, 48), TRIPLE, -16777216),
							process(getImage.apply(ships, 49), TRIPLE, -16777216),
							process(getImage.apply(ships, 50), TRIPLE, -16777216),
							process(getImage.apply(ships, 51), TRIPLE, -16777216),
							process(getImage.apply(ships, 52), TRIPLE, -16777216),
							process(getImage.apply(ships, 53), TRIPLE, -16777216)),
					asList(process(getImage.apply(ships, 54), TRIPLE, -16777216),
							process(getImage.apply(ships, 55), TRIPLE, -16777216),
							process(getImage.apply(ships, 56), TRIPLE, -16777216),
							process(getImage.apply(ships, 57), TRIPLE, -16777216),
							process(getImage.apply(ships, 58), TRIPLE, -16777216),
							process(getImage.apply(ships, 59), TRIPLE, -16777216)),
					asList(process(getImage.apply(ships, 60), TRIPLE, -16777216),
							process(getImage.apply(ships, 61), TRIPLE, -16777216),
							process(getImage.apply(ships, 62), TRIPLE, -16777216),
							process(getImage.apply(ships, 63), TRIPLE, -16777216),
							process(getImage.apply(ships, 64), TRIPLE, -16777216),
							process(getImage.apply(ships, 65), TRIPLE, -16777216)),
					asList(process(getImage.apply(ships, 67), TRIPLE, -16777216),
							process(getImage.apply(ships, 68), TRIPLE, -16777216),
							process(getImage.apply(ships, 69), TRIPLE, -16777216),
							process(getImage.apply(ships, 70), TRIPLE, -16777216),
							process(getImage.apply(ships, 71), TRIPLE, -16777216),
							process(getImage.apply(ships, 72), TRIPLE, -16777216)));

			final BufferedImage planetsSheet = SpriteSheetGenerator.generate(3, true,
					asList(process(getImage.apply(planets, 0), TRIPLE, -16777216),
							process(getImage.apply(planets, 1), TRIPLE, -16777216),
							process(getImage.apply(planets, 2), TRIPLE, -16777216),
							process(getImage.apply(planets, 3), TRIPLE, -16777216),
							process(getImage.apply(planets, 4), TRIPLE, -16777216),
							process(getImage.apply(planets, 5), TRIPLE, -16777216),
							process(getImage.apply(planets, 6), TRIPLE, -16777216)),
					asList(process(getImage.apply(planets, 7), TRIPLE, -16777216),
							process(getImage.apply(planets, 8), TRIPLE, -16777216),
							process(getImage.apply(planets, 9), TRIPLE, -16777216),
							process(getImage.apply(planets, 10), TRIPLE, -16777216),
							process(getImage.apply(planets, 11), TRIPLE, -16777216),
							process(getImage.apply(planets, 12), TRIPLE, -16777216),
							process(getImage.apply(planets, 13), TRIPLE, -16777216)),
					asList(process(getImage.apply(planets, 14), TRIPLE, -16777216),
							process(getImage.apply(planets, 15), TRIPLE, -16777216),
							process(getImage.apply(planets, 16), TRIPLE, -16777216),
							process(getImage.apply(planets, 17), TRIPLE, -16777216),
							process(getImage.apply(planets, 18), TRIPLE, -16777216),
							process(getImage.apply(planets, 19), TRIPLE, -16777216),
							process(getImage.apply(planets, 20), TRIPLE, -16777216)),
					asList(process(getImage.apply(planets, 21), TRIPLE, -16777216),
							process(getImage.apply(planets, 22), TRIPLE, -16777216),
							process(getImage.apply(planets, 23), TRIPLE, -16777216),
							process(getImage.apply(planets, 24), TRIPLE, -16777216),
							process(getImage.apply(planets, 25), TRIPLE, -16777216),
							process(getImage.apply(planets, 26), TRIPLE, -16777216),
							process(getImage.apply(planets, 27), TRIPLE, -16777216)),
					asList(process(getImage.apply(planets, 28), TRIPLE, -16777216),
							process(getImage.apply(planets, 29), TRIPLE, -16777216),
							process(getImage.apply(planets, 30), TRIPLE, -16777216),
							process(getImage.apply(planets, 31), TRIPLE, -16777216),
							process(getImage.apply(planets, 32), TRIPLE, -16777216),
							process(getImage.apply(planets, 33), TRIPLE, -16777216),
							process(getImage.apply(planets, 34), TRIPLE, -16777216)));

			final File targetDir = Optional.of(new File(".").getCanonicalFile())
				.map(wd -> "target".equals(wd.getName().toLowerCase()) ? wd : new File(wd, "target"))
				.get();
			final Path themeZip = Path.of(targetDir.getAbsolutePath(), "moo-theme.zip");

			writeZipFile(themeZip, sheetEntry("fleets.png", fleetsSheet), sheetEntry("stars.png", starsSheet),
					sheetEntry("stars-small.png", starsSmallSheet), sheetEntry("ships-blue.png", shipsBlueSheet),
					sheetEntry("ships-white.png", shipsWhiteSheet), sheetEntry("ships-yellow.png", shipsYellowSheet),
					sheetEntry("planets.png", planetsSheet));
			logger.log(Level.INFO, "Wrote ZIP file to ''{0}''.", themeZip);
		}
		catch (UncheckedIOException ex) {
			throw ex.getCause();
		}
	}

	@SuppressWarnings("checkstyle:CyclomaticComplexity")
	private static File getAndValidateOrionDir(final String[] args) {
		final Optional<File> dfendReloadedDir = Optional.of(DFEND_RELOADED_ORION_DIR).filter(File::exists);
		final Optional<File> argsDir = Optional
			.ofNullable(args.length > 0 && args[0] != null ? new File(args[0]) : null)
			.filter(File::exists);

		File orionDir = null;
		if (argsDir.isEmpty() && dfendReloadedDir.isPresent()) {
			logger.log(Level.INFO,
					"No valid orion dir was passed as argument but D-Fend Reloaded installation was found (''{0}'').",
					dfendReloadedDir.get());
			orionDir = dfendReloadedDir.get();
		}
		else if (argsDir.isEmpty() && dfendReloadedDir.isEmpty()) {
			logger.log(Level.SEVERE,
					"Neither was a valid orion dir passed as argument nor was a D-Fend Reloaded installation found (''{0}'').",
					dfendReloadedDir.get());
			System.exit(-1);
		}
		else if (argsDir.isPresent() && dfendReloadedDir.isPresent()) {
			logger.log(Level.INFO,
					"Orion dir passed as argument (''{0}'') is used. The D-Fend Reloaded installation (''{1}'') is ignored.",
					new Object[] { argsDir.get(), dfendReloadedDir.get() });
			orionDir = argsDir.get();
		}
		else if (argsDir.isPresent() && dfendReloadedDir.isEmpty()) {
			logger.log(Level.INFO, "Orion dir passed as argument (''{0}'') is used.", argsDir.get());
			orionDir = argsDir.get();
		}

		for (final String lbxFileName : LBX_FILES) {
			final File lbxFile = new File(orionDir, lbxFileName);
			if (!lbxFile.exists()) {
				logger.log(Level.SEVERE,
						"The LBX file ''{0}'' was not found in the orion directory. Is the directory a valid Master of Orion installation?",
						lbxFileName);
				System.exit(-1);
			}
		}

		return orionDir;
	}

	@SafeVarargs
	private static void writeZipFile(final Path targetDir, final Entry<String, BufferedImage>... entries)
			throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(targetDir))) {
			final Set<String> processed = new HashSet<>();

			for (final Entry<String, BufferedImage> entry : entries) {
				final String path = entry.getKey();

				String currentPath = "";
				for (final String fragment : path.split("/")) {
					currentPath = currentPath + fragment + (!fragment.contains(".") ? "/" : "");

					if (fragment.contains(".")) {
						final ZipEntry zipEntry = new ZipEntry(currentPath);
						zos.putNextEntry(zipEntry);

						final BufferedImage image = entry.getValue();
						final ByteArrayOutputStream baos = new ByteArrayOutputStream(
								image.getWidth() * image.getHeight());
						ImageIO.write(image, "png", baos);
						zos.write(baos.toByteArray());
						zos.closeEntry();
					}
					else if (processed.add(currentPath)) {
						final ZipEntry zipEntry = new ZipEntry(currentPath);
						zos.putNextEntry(zipEntry);
					}
				}
			}
		}
	}

	private static Entry<String, BufferedImage> sheetEntry(final String path, final BufferedImage image) {
		return new AbstractMap.SimpleImmutableEntry<>(path, image);
	}

	private static Paintable process(final BufferedImage image, final Scale scale, final Integer transparentColor) {
		return ImageProcessor.process(image, scale, transparentColor);
	}

}
