package com.scheible.risingempire.webapp.notification;

import com.scheible.pocketsaw.api.SubModule;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Jackson;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireGame;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireUtil;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Slf4j;
import com.scheible.risingempire.webapp.ExternalFunctionalities.SpringFramework;

/**
 *
 * @author sj
 */
@SubModule(includeSubPackages = false, uses = { RisingEmpireGame.class, RisingEmpireUtil.class, SpringFramework.class,
		Slf4j.class, Jackson.class })
public final class NotificationSubModule {

	private NotificationSubModule() {
	}
}
