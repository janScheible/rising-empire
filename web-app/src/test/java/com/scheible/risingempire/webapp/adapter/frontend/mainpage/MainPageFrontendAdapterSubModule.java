package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import com.scheible.pocketsaw.api.SubModule;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Jackson;
import com.scheible.risingempire.webapp.ExternalFunctionalities.JakartaServletApi;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireGame;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireUtil;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Slf4j;
import com.scheible.risingempire.webapp.ExternalFunctionalities.SpringFramework;
import com.scheible.risingempire.webapp.adapter.frontend.FrontendAdapterSubModule;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.AnnotationFrontendAdapterSubModule;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendAdapterContextSubModule;
import com.scheible.risingempire.webapp.adapter.frontend.dto.FrontendAdapterDtoSubModule;
import com.scheible.risingempire.webapp.game.GameSubModule;
import com.scheible.risingempire.webapp.hypermedia.HypermediaSubModule;
import com.scheible.risingempire.webapp.partial.PartialSubModule;

/**
 *
 * @author sj
 */
@SubModule(uses = { SpringFramework.class, RisingEmpireUtil.class, RisingEmpireGame.class, Jackson.class,
		HypermediaSubModule.class, HypermediaSubModule.class, FrontendAdapterContextSubModule.class,
		AnnotationFrontendAdapterSubModule.class, FrontendAdapterSubModule.class, PartialSubModule.class,
		FrontendAdapterDtoSubModule.class, Slf4j.class, GameSubModule.class, JakartaServletApi.class })
public final class MainPageFrontendAdapterSubModule {

	private MainPageFrontendAdapterSubModule() {
	}
}
