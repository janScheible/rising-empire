package com.scheible.risingempire.webapp;

import com.scheible.pocketsaw.api.SubModule;
import com.scheible.risingempire.webapp.ExternalFunctionalities.ApacheCommonsLogging;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Browscap;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Jackson;
import com.scheible.risingempire.webapp.ExternalFunctionalities.JakartaServletApi;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireGame;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireUtil;
import com.scheible.risingempire.webapp.ExternalFunctionalities.SpringFramework;
import com.scheible.risingempire.webapp.adapter.frontend.FrontendAdapterSubModule;
import com.scheible.risingempire.webapp.game.GameSubModule;
import com.scheible.risingempire.webapp.notification.NotificationSubModule;

/**
 *
 * @author sj
 */
@SubModule(includeSubPackages = false, uses = { RisingEmpireGame.class, FrontendAdapterSubModule.class,
		NotificationSubModule.class, SpringFramework.class, Jackson.class, RisingEmpireUtil.class, GameSubModule.class,
		Browscap.class, JakartaServletApi.class,
		/* used by super-class of WebContentInterceptor */ ApacheCommonsLogging.class })
public final class ApplicationSubModule {

	private ApplicationSubModule() {
	}
}
