package com.scheible.risingempire.game.api.view.fleet;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author sj
 */
public record FleetId(String value) {

	private static final Predicate<String> GAME2_ID_PREDICATE = Pattern.compile("^[ft][BGPRWY]\\d+?").asPredicate();

	/**
	 * @throws IllegalArgumentException if the string does not contain a valid id.
	 */
	public FleetId(String value) {
		if (!GAME2_ID_PREDICATE.test(value)) {
			throw new IllegalArgumentException("The fleet id '" + value + "' is invalid!");
		}
		else {
			this.value = value;
		}
	}

}
