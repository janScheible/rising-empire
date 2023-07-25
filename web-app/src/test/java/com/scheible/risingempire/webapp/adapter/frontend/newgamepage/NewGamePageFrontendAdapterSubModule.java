package com.scheible.risingempire.webapp.adapter.frontend.newgamepage;

import com.scheible.pocketsaw.api.SubModule;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Jackson;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireGame;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireUtil;
import com.scheible.risingempire.webapp.ExternalFunctionalities.SpringFramework;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.AnnotationFrontendAdapterSubModule;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendAdapterContextSubModule;
import com.scheible.risingempire.webapp.game.GameSubModule;
import com.scheible.risingempire.webapp.hypermedia.HypermediaSubModule;

/**
 *
 * @author sj
 */
@SubModule(uses = { SpringFramework.class, RisingEmpireGame.class, FrontendAdapterContextSubModule.class,
		HypermediaSubModule.class, RisingEmpireUtil.class, GameSubModule.class,
		AnnotationFrontendAdapterSubModule.class, Jackson.class })
public final class NewGamePageFrontendAdapterSubModule {

	private NewGamePageFrontendAdapterSubModule() {
	}
}
