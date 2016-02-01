package com.scheible.risingempire.game.core.star;

import java.util.ArrayList;

/**
 *
 * @author sj
 */
public class Stars extends ArrayList<Star> {
	
	public Star getByName(String star) {
		for(Star currentStar : this) {
			if(currentStar.getName().equals(star)) {
				return currentStar;
			}
		}
		
		throw new IllegalStateException("The star '" + star + "' is unknown!");
	}
}
