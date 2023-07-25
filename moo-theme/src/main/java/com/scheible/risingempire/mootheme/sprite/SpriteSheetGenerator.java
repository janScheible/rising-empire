package com.scheible.risingempire.mootheme.sprite;

import java.awt.image.BufferedImage;
import java.util.List;

import com.scheible.risingempire.mootheme.canvas.Canvas;
import com.scheible.risingempire.mootheme.canvas.Paintable;

/**
 *
 * @author sj
 */
public class SpriteSheetGenerator {

	@SafeVarargs
	public static BufferedImage generate(final int margin, final boolean collapseMargin,
			final List<Paintable>... paintables) {
		final int columns = paintables[0].size();
		final int rows = paintables.length;

		final int spriteWidth = paintables[0].get(0).getWidth();
		final int spriteHeight = paintables[0].get(0).getHeight();

		final int effectiveMargin = collapseMargin ? margin : 2 * margin;
		final int startOffset = collapseMargin ? 0 : -margin;

		final int width = 2 * startOffset + effectiveMargin * (columns + 1) + columns * spriteWidth;
		final int height = 2 * startOffset + effectiveMargin * (rows + 1) + rows * spriteHeight;

		final Canvas result = Canvas.createWithPinkBackground(width, height);

		for (int y = 0; y < paintables.length; y++) {
			final List<Paintable> row = paintables[y];
			if (y != paintables.length - 1 && row.size() != columns) {
				throw new IllegalStateException("Only the last row is allowed to have less columns!");
			}

			for (int x = 0; x < row.size(); x++) {
				final Paintable sprite = row.get(x);
				sprite.paint(result, startOffset + effectiveMargin * (x + 1) + x * spriteWidth,
						startOffset + effectiveMargin * (y + 1) + y * spriteHeight);
			}
		}

		return result.getImage();
	}
}
