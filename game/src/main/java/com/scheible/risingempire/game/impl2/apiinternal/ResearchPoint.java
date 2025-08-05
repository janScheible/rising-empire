package com.scheible.risingempire.game.impl2.apiinternal;

public record ResearchPoint(int quantity) {

	public ResearchPoint add(ResearchPoint augend) {
		return new ResearchPoint(this.quantity + augend.quantity);
	}
}
