package com.scheible.risingempire.mootheme.canvas;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author sj
 */
public interface Canvas {

	static Canvas wrap(BufferedImage image) {
		final WritableRaster alphaRaster = image.getAlphaRaster();

		return new Canvas() {
			private static final int[] TRANSPARENT = { 0 };

			@Override
			public void setRGB(final int x, final int y, final int rgb) {
				image.setRGB(x, y, rgb);
			}

			@Override
			public void setTransparent(final int x, final int y) {
				alphaRaster.setPixel(x, y, TRANSPARENT);
			}

			@Override
			public BufferedImage getImage() {
				return image;
			}
		};
	}

	static Canvas createWithPinkBackground(final int width, final int height) {
		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		final Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(new Color(255, 0, 255));
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		graphics.dispose();

		return wrap(image);
	}

	void setRGB(int x, int y, int rgb);

	void setTransparent(int x, int y);

	BufferedImage getImage();
}
