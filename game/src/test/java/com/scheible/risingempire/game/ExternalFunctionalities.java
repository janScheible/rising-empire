package com.scheible.risingempire.game;

import com.scheible.pocketsaw.api.ExternalFunctionality;

/**
 *
 * @author sj
 */
public class ExternalFunctionalities {

	@ExternalFunctionality(packageMatchPattern = { "com.scheible.risingempire.util.**" })
	public static class RisingEmpireUtils {
	}
}
