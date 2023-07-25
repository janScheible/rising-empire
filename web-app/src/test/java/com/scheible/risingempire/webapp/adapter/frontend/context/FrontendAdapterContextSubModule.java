package com.scheible.risingempire.webapp.adapter.frontend.context;

import com.scheible.pocketsaw.api.SubModule;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireGame;
import com.scheible.risingempire.webapp.ExternalFunctionalities.SpringFramework;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.AnnotationFrontendAdapterSubModule;
import com.scheible.risingempire.webapp.game.GameSubModule;
import com.scheible.risingempire.webapp.hypermedia.HypermediaSubModule;

/**
 *
 * @author sj
 */
@SubModule(uses = { SpringFramework.class, RisingEmpireGame.class, AnnotationFrontendAdapterSubModule.class,
		HypermediaSubModule.class, GameSubModule.class })
public final class FrontendAdapterContextSubModule {

	private FrontendAdapterContextSubModule() {
	}
}
