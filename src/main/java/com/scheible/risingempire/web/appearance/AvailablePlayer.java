package com.scheible.risingempire.web.appearance;

import java.awt.Color;

/**
 *
 * @author sj
 */
public enum AvailablePlayer {
	
	HUMAN("Alexander", "Human", "Sol", Color.RED),
	PSILON("Kelvan", "Psilon", "Mentar", Color.CYAN),
	MEKLAR("M5-35", "Meklar", "Meklon", Color.YELLOW),
	SILICOID("Crystous", "Silicoid", "Cryslon", Color.GREEN);	

	private final String leaderName;
	private final String nation;
	private final String homeStar;
	private final Color color;
	
	private AvailablePlayer(String leaderName, String nation, String homeStar, Color color) {
		this.leaderName = leaderName;
		this.nation = nation;
		this.homeStar = homeStar;
		this.color = color;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public String getNation() {
		return nation;
	}

	public String getHomeStar() {
		return homeStar;
	}

	public Color getColor() {
		return color;
	}
	
	public static AvailablePlayer find(String name, String nation) {
		for(AvailablePlayer availablePlayer : values()) {
			if(availablePlayer.getLeaderName().equals(name) && availablePlayer.getNation().equals(nation)) {
				return availablePlayer;
			}
		}
		
		throw new IllegalStateException("The player '" + name + "' with the nation '" + nation + "' does not exist!");
	}
}
