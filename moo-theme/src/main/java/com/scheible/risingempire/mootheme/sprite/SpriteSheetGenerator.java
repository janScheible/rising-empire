package com.scheible.risingempire.mootheme.sprite;

import java.awt.image.BufferedImage;
import java.util.List;

import com.scheible.risingempire.mootheme.canvas.Canvas;
import com.scheible.risingempire.mootheme.canvas.Paintable;

/**
 * @author sj
 */
public class SpriteSheetGenerator {

	@SafeVarargs
	public static BufferedImage generate(int margin, boolean collapseMargin, List<Paintable>... paintables) {
		int columns = paintables[0].size();
		int rows = paintables.length;

		int spriteWidth = paintables[0].get(0).getWidth();
		int spriteHeight = paintables[0].get(0).getHeight();

		int effectiveMargin = collapseMargin ? margin : 2 * margin;
		int startOffset = collapseMargin ? 0 : -margin;

		int width = 2 * startOffset + effectiveMargin * (columns + 1) + columns * spriteWidth;
		int height = 2 * startOffset + effectiveMargin * (rows + 1) + rows * spriteHeight;

		Canvas result = Canvas.createWithPinkBackground(width, height);

		for (int y = 0; y < paintables.length; y++) {
			List<Paintable> row = paintables[y];
			if (y != paintables.length - 1 && row.size() != columns) {
				throw new IllegalStateException("Only the last row is allowed to have less columns!");
			}

			for (int x = 0; x < row.size(); x++) {
				Paintable sprite = row.get(x);
				sprite.paint(result, startOffset + effectiveMargin * (x + 1) + x * spriteWidth,
						startOffset + effectiveMargin * (y + 1) + y * spriteHeight);
			}
		}

		return result.getImage();
	}

}
