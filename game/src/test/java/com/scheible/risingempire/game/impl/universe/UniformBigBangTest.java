package com.scheible.risingempire.game.impl.universe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.universe.Location;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class UniformBigBangTest {

	private static final Color[] STAR_COLORS = new Color[] { Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE,
			Color.WHITE, new Color(156, 0, 156) };

	@Test
	void testHugeBigBang() throws IOException {
		GalaxySize galaxySize = GalaxySize.HUGE;
		Set<Location> locations = new UniformBigBang().getSystemLocations(galaxySize, 200);

		BufferedImage image = new BufferedImage(galaxySize.width(), galaxySize.height(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = image.createGraphics();

		graphics2D.setPaint(Color.BLACK);
		graphics2D.fillRect(0, 0, galaxySize.width(), galaxySize.height());

		for (Location location : locations) {
			graphics2D.setPaint(STAR_COLORS[ThreadLocalRandom.current().nextInt(STAR_COLORS.length)]);
			graphics2D.fillOval(location.x(), location.y(), 40, 40);
		}

		ImageIO.write(image, "png", new File("./target/huge-poisson-disc-sampling-big-bang.png"));

		assertThat(locations).hasSizeGreaterThan(30);
	}

}
