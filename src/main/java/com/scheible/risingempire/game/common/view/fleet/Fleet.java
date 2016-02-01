package com.scheible.risingempire.game.common.view.fleet;

/**
 *
 * @author sj
 */
public class Fleet {
	
	private final int id;
	private final String nation;
	
	private final String star;
	
	private final Integer x;
	private final Integer y;
	
	private final boolean dispatchable;
	

	private Fleet(int id, int x, int y, String nation, boolean dispatchable) {
		this.id = id;
		this.nation = nation;
		
		this.star = null;
		this.x = x;
		this.y = y;
		
		this.dispatchable = dispatchable;
	}	
	
	private Fleet(int id, String star, String nation, boolean dispatchable) {
		this.id = id;
		this.nation = nation;
		
		this.star = star;
		this.x = null;
		this.y = null;		
		
		this.dispatchable = dispatchable;
	}
	
	public static Fleet createOrbitingFleet(int id, String star, String nation, boolean dispatchable) {
		Fleet fleet = new Fleet(id, star, nation, dispatchable);
		return fleet;				
	}
	
	public static Fleet createTravelingFleet(int id, int x, int y, String nation, boolean dispatchable) {
		Fleet fleet = new Fleet(id, x, y, nation, dispatchable);
		return fleet;
	}	

	public int getId() {
		return id;
	}

	public String getStar() {
		return star;
	}

	public String getNation() {
		return nation;
	}

	public boolean isDispatchable() {
		return dispatchable;
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}
}
