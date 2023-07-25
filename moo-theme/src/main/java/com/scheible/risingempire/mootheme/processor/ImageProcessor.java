package com.scheible.risingempire.mootheme.processor;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Optional;

import com.scheible.risingempire.mootheme.canvas.Canvas;
import com.scheible.risingempire.mootheme.canvas.Paintable;

/**
 *
 * @author sj
 */
public class ImageProcessor {

	public enum Scale {

		SINGLE(1), DOUBLE(2), TRIPLE(3);

		final int factor;

		Scale(final int factor) {
			this.factor = factor;
		}

		public int getFactor() {
			return factor;
		}
	}

	public static Paintable process(final BufferedImage image, final Scale scale) {
		return process(image, scale, Optional.empty(), Optional.empty());
	}

	public static Paintable process(final BufferedImage image, final Scale scale, final Integer transparentColor) {
		return process(image, scale, Optional.of(transparentColor), Optional.empty());
	}

	public static Paintable process(final BufferedImage image, final Scale scale, final Rectangle cropRect) {
		return process(image, scale, Optional.empty(), Optional.of(cropRect));
	}

	public static Paintable process(final BufferedImage image, final Scale scale, final Integer transparentColor,
			final Rectangle cropRect) {
		return process(image, scale, Optional.of(transparentColor), Optional.of(cropRect));
	}

	private static Paintable process(final BufferedImage image, final Scale scale,
			final Optional<Integer> transparentColor, final Optional<Rectangle> cropRect) {
		final BufferedImage source = cropRect.isPresent()
				? image.getSubimage((int) cropRect.get().getX(), (int) cropRect.get().getY(),
						(int) cropRect.get().getWidth(), (int) cropRect.get().getHeight())
				: image;

		return new Paintable() {
			@Override
			public void paint(final Canvas canvas, final int offsetX, final int offsetY) {
				for (int x = 0; x < source.getWidth(); x++) {
					for (int y = 0; y < source.getHeight(); y++) {
						final int rgb = source.getRGB(x, y);

						for (int x1 = 0; x1 < scale.getFactor(); x1++) {
							for (int y1 = 0; y1 < scale.getFactor(); y1++) {
								final int xCanvas = offsetX + x * scale.getFactor() + x1;
								final int yCanvas = offsetY + y * scale.getFactor() + y1;

								if (transparentColor.isEmpty() || transparentColor.get() != rgb) {
									canvas.setRGB(xCanvas, yCanvas, rgb);
								} else if (transparentColor.isPresent()) {
									canvas.setTransparent(xCanvas, yCanvas);
								}
							}
						}
					}
				}
			}

			@Override
			public int getWidth() {
				return source.getWidth() * scale.getFactor();
			}

			@Override
			public int getHeight() {
				return source.getHeight() * scale.getFactor();
			}
		};
	}
}
