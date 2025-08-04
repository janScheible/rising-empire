package com.scheible.risingempire.game.api._testgame;

import com.scheible.risingempire.game.api.view.system.SystemId;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author sj
 */
@ExtendWith(value = TestScenarioRunnerExtension.class)
public abstract class AbstractGameTest {

	public static SystemId SOL_BLUE_HOME = new SystemId("s800x800");

	public static SystemId KRYLON_WHITE_HOME = new SystemId("s2933x1333");

	public static SystemId SPICIA_WHITE = new SystemId("s5066x4000");

	public static SystemId AJAX = new SystemId("s2400x2933");

}
