package com.scheible.risingempire.webapp.adapter.frontend;

import com.scheible.pocketsaw.api.SubModule;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Browscap;
import com.scheible.risingempire.webapp.ExternalFunctionalities.EsBuildBindings;
import com.scheible.risingempire.webapp.ExternalFunctionalities.EsBuildSpringDevServer;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Jackson;
import com.scheible.risingempire.webapp.ExternalFunctionalities.JakartaServletApi;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireGame;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Slf4j;
import com.scheible.risingempire.webapp.ExternalFunctionalities.SpringFramework;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.AnnotationFrontendAdapterSubModule;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendAdapterContextSubModule;
import com.scheible.risingempire.webapp.adapter.frontend.dto.FrontendAdapterDtoSubModule;
import com.scheible.risingempire.webapp.game.GameSubModule;
import com.scheible.risingempire.webapp.hypermedia.HypermediaSubModule;
import com.scheible.risingempire.webapp.notification.NotificationSubModule;

/**
 * @author sj
 */
@SubModule(includeSubPackages = false,
		uses = { RisingEmpireGame.class, EsBuildSpringDevServer.class, FrontendAdapterContextSubModule.class,
				AnnotationFrontendAdapterSubModule.class, GameSubModule.class, NotificationSubModule.class,
				FrontendAdapterDtoSubModule.class, HypermediaSubModule.class, SpringFramework.class,
				JakartaServletApi.class, Slf4j.class, Browscap.class, EsBuildBindings.class, Jackson.class })
public final class FrontendAdapterSubModule {

	private FrontendAdapterSubModule() {
	}

}
