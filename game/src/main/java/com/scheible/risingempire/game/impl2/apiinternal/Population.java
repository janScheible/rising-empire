package com.scheible.risingempire.game.impl2.apiinternal;

import java.util.function.Function;

public record Population(double quantity) implements Comparable<Population> {

	private static final double INCREMENT = 0.201;

	private static final Function<Double, Double> LOGISTIC_FUNCTION = x -> 1 / (1 + Math.exp(-x));

	public Population grow(Population max) {
		// use https://en.wikipedia.org/wiki/Logistic_function#Inverse_function to
		// calcualte the current x
		double currentP = quantity() / max.quantity();
		double currentX = Math.log(currentP / (1 - currentP));

		// increment is selected to have a 10% growth at half of max population (same as
		// in the original Master of Orion)
		return new Population(LOGISTIC_FUNCTION.apply(currentX + INCREMENT) * max.quantity());
	}

	public Population subtract(Population subtrahend) {
		return new Population(this.quantity - subtrahend.quantity);
	}

	public Population add(Population augend, Population max) {
		return new Population(Math.min(max.quantity, this.quantity + augend.quantity));
	}

	@Override
	public int compareTo(Population o) {
		return Double.compare(this.quantity, o.quantity);
	}

}
