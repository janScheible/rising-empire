package com.scheible.risingempire.mootheme.processor;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import com.scheible.risingempire.mootheme.canvas.Canvas;
import com.scheible.risingempire.mootheme.canvas.Paintable;
import com.scheible.risingempire.mootheme.processor.ImageProcessor.Scale;

/**
 *
 * @author sj
 */
public class ImageProcessorTest {

	@Test
	void testScaleWithCropAndTransparency() throws IOException {
		BufferedImage testImage = ImageIO.read(getClass().getResourceAsStream("processor-test-image.png"));

		Paintable paintable = ImageProcessor.process(testImage, Scale.TRIPLE, -65316, new Rectangle(1, 1, 2, 2));
		Canvas result = Canvas.createWithPinkBackground(paintable.getWidth(), paintable.getHeight());
		paintable.paint(result, 0, 0);

		ImageIO.write(result.getImage(), "png", new File("./target/scaled-processor-test-image.png"));

		assertThat(result.getImage().getWidth()).isEqualTo(6);
		assertThat(result.getImage().getHeight()).isEqualTo(6);

		assertThat(new Color(result.getImage().getRGB(1, 1))).isEqualTo(new Color(255, 0, 0));
		assertThat(new Color(result.getImage().getRGB(4, 1))).isEqualTo(new Color(0, 255, 0));
		assertThat(new Color(result.getImage().getRGB(1, 4), true).getAlpha()).isEqualTo(0);
		assertThat(new Color(result.getImage().getRGB(4, 4))).isEqualTo(new Color(255, 0, 0));
	}
}
