package com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage;

import com.scheible.pocketsaw.api.SubModule;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Jackson;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireGame;
import com.scheible.risingempire.webapp.ExternalFunctionalities.SpringFramework;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.AnnotationFrontendAdapterSubModule;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendAdapterContextSubModule;
import com.scheible.risingempire.webapp.adapter.frontend.dto.FrontendAdapterDtoSubModule;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.MainPageFrontendAdapterSubModule;
import com.scheible.risingempire.webapp.hypermedia.HypermediaSubModule;

/**
 *
 * @author sj
 */
@SubModule(uses = { SpringFramework.class, RisingEmpireGame.class, MainPageFrontendAdapterSubModule.class,
		Jackson.class, HypermediaSubModule.class, HypermediaSubModule.class, FrontendAdapterContextSubModule.class,
		AnnotationFrontendAdapterSubModule.class, FrontendAdapterDtoSubModule.class })
public final class SpaceCombatPageFrontendAdapterSubModule {

	private SpaceCombatPageFrontendAdapterSubModule() {
	}
}
