package com.scheible.risingempire.mootheme.sprite;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.scheible.risingempire.mootheme.canvas.Paintable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class SpriteSheetGeneratorTest {

	@Test
	void testSpriteSheetGeneration() throws IOException {
		BufferedImage testImage = ImageIO.read(getClass().getResourceAsStream("sprite.png"));

		BufferedImage result = SpriteSheetGenerator.generate(1, true,
				Arrays.asList(Paintable.wrap(testImage), Paintable.wrap(testImage)),
				Arrays.asList(Paintable.wrap(testImage)));

		ImageIO.write(result, "png", new File("./target/sprite-sheet.png"));

		assertThat(result.getWidth()).isEqualTo(11);
		assertThat(result.getHeight()).isEqualTo(11);
	}

	@Test
	void testSpriteSheetGenerationWithoutCollapsedMargin() throws IOException {
		BufferedImage testImage = ImageIO.read(getClass().getResourceAsStream("sprite.png"));

		BufferedImage result = SpriteSheetGenerator.generate(1, false,
				Arrays.asList(Paintable.wrap(testImage), Paintable.wrap(testImage), Paintable.wrap(testImage)),
				Arrays.asList(Paintable.wrap(testImage)));

		ImageIO.write(result, "png", new File("./target/sprite-sheet-without-collapsed-margin.png"));

		assertThat(result.getWidth()).isEqualTo(18);
		assertThat(result.getHeight()).isEqualTo(12);
	}

}
