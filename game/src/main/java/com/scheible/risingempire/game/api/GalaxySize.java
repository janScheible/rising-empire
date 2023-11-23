package com.scheible.risingempire.game.api;

/**
 * @author sj
 */
public enum GalaxySize {

	SMALL(1024, 768), MEDIUM(1280, 960), LARGE(1440, 1080), HUGE(1920, 1440);

	private final int width;

	private final int height;

	GalaxySize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

}
