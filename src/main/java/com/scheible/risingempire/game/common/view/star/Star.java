package com.scheible.risingempire.game.common.view.star;

/**
 *
 * @author sj
 */
public class Star {
	
	private final String name;
	private final int x;
	private final int y;
	
	private final String nation;
	private final Integer population;

	private Star(String name, int x, int y, String nation, Integer population) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.nation = nation;
		this.population = population;
	}
	
	public static Star createAnonymousStar(String name, int x, int y) {
		Star star = new Star(name, x, y, null, null);
		return star;
	}
	
	public static Star createAlienStar(String name, int x, int y, String nation) {
		Star star = new Star(name, x, y, nation, null);
		return star;
	}
		
	public static Star createOwnedStar(String name, int x, int y, String nation, int population) {
		Star star = new Star(name, x, y, nation, population);
		return star;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getNation() {
		return nation;
	}

	public Integer getPopulation() {
		return population;
	}
}
