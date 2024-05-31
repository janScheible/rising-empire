package com.scheible.risingempire.game.api._testgame;

import com.scheible.risingempire.game.api.view.system.SystemId;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author sj
 */
@ExtendWith(value = TestScenarioRunnerExtension.class)
public abstract class AbstractGameTest {

	public static SystemId SOL_BLUE_HOME = new SystemId("s60x60");

	public static SystemId FIERAS_WHITE_HOME = new SystemId("s220x100");

	public static SystemId SPICIA_WHITE = new SystemId("s380x300");

	public static SystemId AJAX = new SystemId("s180x220");

}
