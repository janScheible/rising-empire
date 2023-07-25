package com.scheible.risingempire.webapp.game;

import com.scheible.pocketsaw.api.SubModule;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireGame;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireUtil;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Slf4j;
import com.scheible.risingempire.webapp.ExternalFunctionalities.SpringFramework;
import com.scheible.risingempire.webapp.adapter.frontend.dto.FrontendAdapterDtoSubModule;
import com.scheible.risingempire.webapp.notification.NotificationSubModule;

/**
 *
 * @author sj
 */
@SubModule(includeSubPackages = false, uses = { RisingEmpireGame.class, RisingEmpireUtil.class,
		NotificationSubModule.class, FrontendAdapterDtoSubModule.class, Slf4j.class, SpringFramework.class })
public final class GameSubModule {

	private GameSubModule() {
	}
}
