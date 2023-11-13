package com.scheible.risingempire.mootheme.canvas;

import java.awt.image.BufferedImage;

/**
 * @author sj
 */
public interface Paintable {

	static Paintable wrap(BufferedImage image) {
		return new Paintable() {
			@Override
			public void paint(final Canvas canvas, final int offsetX, final int offsetY) {
				for (int x = 0; x < image.getWidth(); x++) {
					for (int y = 0; y < image.getHeight(); y++) {
						canvas.setRGB(x + offsetX, y + offsetY, image.getRGB(x, y));
					}
				}
			}

			@Override
			public int getWidth() {
				return image.getWidth();
			}

			@Override
			public int getHeight() {
				return image.getHeight();
			}
		};
	}

	void paint(Canvas canvas, int offsetX, int offsetY);

	int getWidth();

	int getHeight();

}
