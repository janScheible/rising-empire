package com.scheible.risingempire.webapp.adapter.frontend.dto;

import com.scheible.pocketsaw.api.SubModule;
import com.scheible.risingempire.webapp.ExternalFunctionalities.Jackson;
import com.scheible.risingempire.webapp.ExternalFunctionalities.RisingEmpireGame;

/**
 * @author sj
 */
@SubModule(uses = { RisingEmpireGame.class, Jackson.class })
public final class FrontendAdapterDtoSubModule {

	private FrontendAdapterDtoSubModule() {
	}

}
